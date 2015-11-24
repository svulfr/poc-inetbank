package ru.ulfr.poc.modules.atm;

import ru.ulfr.poc.modules.atm.model.DepositRequest;
import ru.ulfr.poc.modules.atm.model.WithdrawRequest;

/**
 * Interface for ATM dao
 */
public interface AtmDao {
    long withdraw(WithdrawRequest request);

    long deposit(DepositRequest request);
}
