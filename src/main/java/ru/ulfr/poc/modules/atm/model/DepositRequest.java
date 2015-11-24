package ru.ulfr.poc.modules.atm.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Money deposit request for ATM
 */
@SuppressWarnings("unused")
public class DepositRequest implements Serializable {
    /**
     * Account ID to deposit to
     */
    private long accountId;

    /**
     * Amount to deposit
     */
    private BigDecimal amount;

    /**
     * Currency ISO 4217 N code
     */
    private int currencyId;

    public DepositRequest() {
    }

    public DepositRequest(long accountId, BigDecimal amount, int currencyId) {
        this.accountId = accountId;
        this.amount = amount;
        this.currencyId = currencyId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }
}
