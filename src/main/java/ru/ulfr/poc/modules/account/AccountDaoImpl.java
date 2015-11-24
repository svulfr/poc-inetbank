package ru.ulfr.poc.modules.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.account.model.AccountSearchCriteria;
import ru.ulfr.poc.modules.bank.model.Currency;
import ru.ulfr.poc.modules.bank.model.TransferOrder;
import ru.ulfr.poc.modules.processor.CurrencyConverter;
import ru.ulfr.poc.modules.processor.model.Transaction;
import ru.ulfr.poc.modules.users.model.User;
import ru.ulfr.poc.modules.utils.AbstractDao;
import ru.ulfr.poc.modules.utils.HTTP404Exception;
import ru.ulfr.poc.modules.utils.HTTP422Exception;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Account management subsystem
 */
@Repository
public class AccountDaoImpl extends AbstractDao implements AccountDao {

    @Autowired
    CurrencyConverter currencyConverter;

    /**
     * Return account for specified account id
     *
     * @param accountId account id
     * @return Account information
     */
    @Override
    public Account getAccount(long accountId) {
        return em.find(Account.class, accountId);
    }

    /**
     * Returns user id by account id
     *
     * @param accountId account id
     * @return user id for valid account or null if account id is invalid
     */
    @Override
    public Long getUserIdByAccount(long accountId) {
        // fetch list to check account id as well
        List<Long> ids = em.createQuery("select user.id from Account where id=?1", Long.class)
                .setParameter(1, accountId)
                .setMaxResults(1)
                .getResultList();
        return ids.size() > 0 ? ids.get(0) : null;
    }

    /**
     * Creates bank account for the specified user
     *
     * @param userId  user id to create account for
     * @param account {@link Account} object describing account parameters
     * @return newly created account
     */
    @Override
    @Transactional
    public Account createAccount(long userId, Account account) {
        // check that user is valid
        User user = em.getReference(User.class, userId);
        if (user == null) {
            throw new HTTP404Exception("user not found");
        }
        // check that currency is valid
        Currency currency = em.getReference(Currency.class, account.getCurrency().getCode());
        if (currency == null) {
            throw new HTTP404Exception("unknown currency");
        }
        // setup parameters and create account
        account.setUser(user);
        account.setAmount(BigDecimal.ZERO);
        account.setCurrency(currency);
        em.persist(account);
        return account;
    }

    /**
     * Returns list of accounts for specified user
     *
     * @param userId id of user to return accounts for
     * @return list of accounts
     */
    @Override
    public List<Account> listAccounts(long userId) {
        return em.createQuery("from Account where user.id = ?1", Account.class)
                .setParameter(1, userId)
                .getResultList();
    }

    /**
     * Performs check that account has sufficient balance in specified currency
     *
     * @param accountId  account id to check
     * @param amount     amount to check
     * @param currencyId currency for amount
     * @return <code>true</code> if balance contains specified amount and <code>false</code> otherwise
     */
    @Override
    public boolean checkBalance(long accountId, BigDecimal amount, int currencyId) {
        Account account = em.find(Account.class, accountId);
        // calculate rate in terms of account currency
        BigDecimal cRate = currencyConverter.getConversionRate(account.getCurrency().getCode(), currencyId);
        BigDecimal amountRequired = amount.divide(cRate, BigDecimal.ROUND_CEILING);
        // check amount in terms of account currency
        return account.getAmount().compareTo(amountRequired) >= 0;
    }

    /**
     * Searches account by specified criteria.
     * Criteria are combined by conjunction, i.e. if criteria element is specified, it is used.
     *
     * @param criteria criteria to search
     * @return list of matching accounts
     */
    @Override
    public List<Account> search(AccountSearchCriteria criteria) {
        // match against forms
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        ArrayList<Predicate> predicates = new ArrayList<>();

        if (criteria.getUserCriteria() != null) {
            List<Long> userIds = em.createQuery("select id from User where name like ?1 or email like ?1", Long.class)
                    .setParameter(1, String.format("%%%s%%", criteria.getUserCriteria()))
                    .getResultList();
            if (userIds.size() > 0) {
                predicates.add(cb.in(root.get("user").get("id")).value(userIds));
            }
        }

        if (criteria.getAccountId() != null) {
            predicates.add(cb.equal(root.get("id"), criteria.getAccountId()));
        }

        if (criteria.getCurrencyCode() != null) {
            predicates.add(cb.equal(root.get("currency").get("code"), criteria.getCurrencyCode()));
        }

        // build and execute query
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        TypedQuery<Account> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    /**
     * Returns specified transaction
     *
     * @param txId transaction id
     * @return transaction or <code>null</code> if transaction is not found
     */
    @Override
    public Transaction getTransaction(long txId) {
        return em.find(Transaction.class, txId);
    }

    /**
     * List transactions by account
     *
     * @param accountId account to list transactions for
     * @return list of transactions
     */
    @Override
    public List<Transaction> listTransactions(long accountId) {
        return em.createQuery("from Transaction where recipientId = ?1 or originId = ?1", Transaction.class)
                .setParameter(1, accountId)
                .getResultList();
    }

    /**
     * Creates new transaction based on order
     *
     * @param accountId id of source account
     * @param order     order with information about transaction
     * @return transactionId
     */
    @Override
    @Transactional
    public long createTransaction(long accountId, TransferOrder order) {
        // fetch source and target accounts
        Account source = em.find(Account.class, accountId);
        Account target = em.find(Account.class, order.getRecipient());
        if (source == null || target == null) {
            throw new HTTP422Exception("unknown account");
        }

        Transaction tx = new Transaction();
        tx.setTxAmount(order.getAmount());
        tx.setTxCurrency(order.getCurrency());
        tx.setTxType(Transaction.TYPE_TRANSFER);
        tx.setState(Transaction.STATE_INITIATED);
        tx.setOriginId(source.getId());
        tx.setRecipientId(target.getId());

        // supplemental info
        tx.setOriginCurrency(source.getCurrency().getCode());
        tx.setRecipientCurrency(target.getCurrency().getCode());
        em.persist(tx);
        return tx.getId();
    }

    /**
     * Returns user by account id
     *
     * @param accountId account id
     * @return {@link User} object
     */
    @Override
    public User getUserByAccount(Long accountId) {
        List<User> users = em.createQuery("select user from Account a where a.id=?1", User.class)
                .setParameter(1, accountId)
                .setMaxResults(1)
                .getResultList();
        return users.size() > 0 ? users.get(0) : null;
    }
}
