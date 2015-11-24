package ru.ulfr.poc.modules.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ulfr.poc.Config;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.account.model.AccountSearchCriteria;
import ru.ulfr.poc.modules.bank.model.TransferOrder;
import ru.ulfr.poc.modules.processor.TransactionProcessor;
import ru.ulfr.poc.modules.processor.model.Transaction;
import ru.ulfr.poc.modules.utils.AbstractController;
import ru.ulfr.poc.modules.utils.HTTP404Exception;

import java.util.List;

/**
 * REST controller for account module
 */
@RestController
@RequestMapping(path = "/rest/account")
@SuppressWarnings("unused")
public class AccountRest extends AbstractController {

    @Autowired
    AccountDao accountDao;

    @Autowired
    TransactionProcessor transactionProcessor;


    /**
     * Returns account by account Id
     * Restricted to admin and account owner
     *
     * @param accountId account id to return
     * @return {@link Account} object
     */
    @RequestMapping(path = "/{accountId}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable long accountId) {
        assertPrivileges(Config.ROLE_USER);
        Account account = accountDao.getAccount(accountId);
        if (account == null) {
            throw new HTTP404Exception("account not found");
        }
        assertUserOrAdmin(account.getUser().getId());
        return account;
    }

    /**
     * Returns list of accounts for current logged user
     * Restricted to users
     *
     * @return list of accounts
     */
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public List<Account> listAccounts() {
        assertPrivileges(Config.ROLE_USER);
        return accountDao.listAccounts(getSessionUser().getId());
    }

    /**
     * List user accounts
     * Method can be invoked by account owner or admin
     */
    @RequestMapping(path = "/{userId}/", method = RequestMethod.GET)
    public List<Account> listUserAccounts(@PathVariable long userId) {
        assertPrivileges(Config.ROLE_USER);
        long callerId = getSessionUser().getId();
        if (userId != callerId) {
            assertPrivileges(Config.ROLE_ADMIN);
        }
        return accountDao.listAccounts(userId);
    }

    /**
     * Performs searching accounts by specified criteria object
     * Restricted to admin users only
     *
     * @param criteria criteria
     * @return list of accounts matching the criteria
     */
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public List<Account> searchAccounts(@RequestBody AccountSearchCriteria criteria) {
        assertPrivileges(Config.ROLE_ADMIN);
        return accountDao.search(criteria);
    }

    /**
     * Creates account for user
     */
    @RequestMapping(path = "/{userId}/", method = RequestMethod.POST)
    public Account createAccount(@PathVariable long userId,
                                 @RequestBody Account account) {
        assertPrivileges(Config.ROLE_ADMIN);
        return accountDao.createAccount(userId, account);
    }

    @RequestMapping(path = "/{accountId}/tx/{txId}", method = RequestMethod.GET)
    public Transaction getTransaction(@PathVariable long accountId,
                                      @PathVariable long txId) {
        assertPrivileges(Config.ROLE_USER);
        Account account = this.getAccount(accountId); // will throw 403 if account is restricted in this context
        Transaction tx = accountDao.getTransaction(txId);
        if (tx.getTxType() == Transaction.TYPE_TRANSFER) {
            tx.setParty(accountDao.getUserByAccount(tx.getOriginId() == accountId ? tx.getRecipientId() : tx.getOriginId()));
        }
        return tx;
    }

    /**
     * Returns list of transactions for specified account
     * Restricted to admin user or account owner only
     *
     * @param accountId account id to list transactions for
     * @return list of transactions
     */
    @RequestMapping(path = "/{accountId}/tx", method = RequestMethod.GET)
    public List<Transaction> listTransactions(@PathVariable long accountId) {

        // fetch account to get information about owner
        Account account = accountDao.getAccount(accountId);
        if (account == null) {
            throw new HTTP404Exception("account not found");
        }

        // check that invoked by admin or account owner
        assertUserOrAdmin(account.getUser().getId());

        // return list of transactions
        return accountDao.listTransactions(accountId);
    }

    /**
     * Performs transfer according to specified order
     *
     * @param userId        user id that transfers amount
     * @param accountId     account id to use as source
     * @param transferOrder order describing recipient
     * @return <code>true</code>
     */
    @RequestMapping(path = "/{userId}/{accountId}/transfer", method = RequestMethod.POST)
    public long transferAmount(@PathVariable long userId,
                               @PathVariable long accountId,
                               @RequestBody TransferOrder transferOrder) {
        assertPrivileges(Config.ROLE_USER);
        assertUser(userId);
        getAccount(accountId); // performs checks on availability of the account in current context
        long txId = accountDao.createTransaction(accountId, transferOrder);
        // code below can also be triggered elsewhere (e.g. DB trigger, JMS message, etc.)
        transactionProcessor.processTransaction(txId);
        return txId;
    }

}
