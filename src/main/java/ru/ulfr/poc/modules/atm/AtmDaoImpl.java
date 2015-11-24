package ru.ulfr.poc.modules.atm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.modules.account.AccountDao;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.atm.model.DepositRequest;
import ru.ulfr.poc.modules.atm.model.WithdrawRequest;
import ru.ulfr.poc.modules.processor.model.Transaction;
import ru.ulfr.poc.modules.utils.AbstractDao;

/**
 * Implementation for ATM
 */
@Repository
public class AtmDaoImpl extends AbstractDao implements AtmDao {


    @Autowired
    AccountDao accountDao;

    /**
     * Checks amount and registers withdrawal transaction
     *
     * @param request request
     * @return transactionId or -1 on error
     */
    @Override
    @Transactional
    public long withdraw(WithdrawRequest request) {

        Account account = accountDao.getAccount(request.getAccountId());

        // check balance
        if (!accountDao.checkBalance(request.getAccountId(), request.getAmount(), request.getCurrencyId())) {
            return -1;
        }

        // register transaction
        Transaction tx = new Transaction();
        tx.setTxType(Transaction.TYPE_WITHDRAW);
        tx.setState(Transaction.STATE_INITIATED);
        tx.setTxAmount(request.getAmount());
        tx.setTxCurrency(request.getCurrencyId());
        tx.setOriginId(request.getAccountId());
        tx.setOriginCurrency(account.getCurrency().getCode());
        em.persist(tx);
        return tx.getId();
    }


    /**
     * Checks amount and registers deposit transaction
     *
     * @param request request
     * @return transactionId
     */
    @Override
    @Transactional
    public long deposit(DepositRequest request) {

        Account account = accountDao.getAccount(request.getAccountId());

        // register transaction
        Transaction tx = new Transaction();
        tx.setTxType(Transaction.TYPE_DEPOSIT);
        tx.setState(Transaction.STATE_INITIATED);
        tx.setTxAmount(request.getAmount());
        tx.setTxCurrency(request.getCurrencyId());
        tx.setRecipientId(request.getAccountId());
        tx.setRecipientCurrency(account.getCurrency().getCode());
        em.persist(tx);
        return tx.getId();
    }
}
