package ru.ulfr.poc.modules.atm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.ulfr.poc.modules.account.AccountDao;
import ru.ulfr.poc.modules.account.AccountRest;
import ru.ulfr.poc.modules.atm.model.DepositRequest;
import ru.ulfr.poc.modules.atm.model.WithdrawRequest;
import ru.ulfr.poc.modules.bank.BankDao;
import ru.ulfr.poc.modules.processor.TransactionProcessor;
import ru.ulfr.poc.modules.utils.AbstractController;
import ru.ulfr.poc.modules.utils.HTTP422Exception;

/**
 * Provides REST interface for typical ATM functions: deposit and withdraw
 */
@RestController
@RequestMapping(path = "/rest/atm")
@SuppressWarnings("unused")
public class AtmRest extends AbstractController {

    @Autowired
    AtmDao atmDao;

    @Autowired
    AccountDao accountDao;

    @Autowired
    BankDao bankDao;

    @Autowired
    AccountRest accountRest;

    @Autowired
    TransactionProcessor transactionProcessor;

    /**
     * Handles amount withdraw request
     *
     * @param withdraw accountId, amount and currency to withdraw
     * @return txId if transaction initiated OK and -1 otherwise
     * @throws HTTP422Exception in case of action failure
     */
    @RequestMapping(path = "/withdraw", method = RequestMethod.PUT)
    public long withdraw(@RequestBody WithdrawRequest withdraw) {
        accountRest.getAccount(withdraw.getAccountId()); // checks account availability in current context
        long txId = atmDao.withdraw(withdraw);
        if (txId < 0) {
            throw new HTTP422Exception("invalid state");
        }
        transactionProcessor.processTransaction(txId);
        return txId;
    }

    /**
     * Handles amount deposit request
     *
     * @param deposit accountId, amount and currency to deposit
     * @return txId if transaction initiated OK and -1 otherwise
     * @throws HTTP422Exception in case of action failure
     */
    @RequestMapping(path = "/deposit", method = RequestMethod.PUT)
    public long deposit(@RequestBody DepositRequest deposit) {
        accountRest.getAccount(deposit.getAccountId()); // checks account availability in current context
        long txId = atmDao.deposit(deposit);
        if (txId < 0) {
            throw new HTTP422Exception("invalid state");
        }
        transactionProcessor.processTransaction(txId);
        return txId;
    }
}
