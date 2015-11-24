package ru.ulfr.poc.modules.atm.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Money withdraw request for ATM
 */
@SuppressWarnings("unused")
public class WithdrawRequest implements Serializable {
    /**
     * Account ID to withdraw from
     */
    private long accountId;

    /**
     * Amount to withdraw
     */
    private BigDecimal amount;

    /**
     * Currency ISO 4217 N code
     */
    private int currencyId;

    public WithdrawRequest() {
    }

    public WithdrawRequest(long accountId, BigDecimal amount, int currencyId) {
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
