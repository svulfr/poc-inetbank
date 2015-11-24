package ru.ulfr.poc.modules.account;

import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.account.model.AccountSearchCriteria;
import ru.ulfr.poc.modules.bank.model.TransferOrder;
import ru.ulfr.poc.modules.processor.model.Transaction;
import ru.ulfr.poc.modules.users.model.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for account support system
 */
public interface AccountDao {

    Account getAccount(long accountId);

    Long getUserIdByAccount(long accountId);

    @Transactional
    Account createAccount(long userId, Account account);

    List<Account> listAccounts(long userId);

    boolean checkBalance(long accountId, BigDecimal amount, int currencyId);

    List<Account> search(AccountSearchCriteria criteria);

    Transaction getTransaction(long txId);

    List<Transaction> listTransactions(long accountId);

    @Transactional
    long createTransaction(long accountId, TransferOrder order);

    User getUserByAccount(Long accountId);
}
