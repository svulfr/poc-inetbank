package ru.ulfr.poc.modules.account.model;

import java.io.Serializable;

/**
 * Represents search criteria for accounts
 */
public class AccountSearchCriteria implements Serializable {
    /**
     * Customer name or email (part)
     */
    String userCriteria;

    /**
     * Customer's Account id
     */
    Long accountId;

    /**
     * Currency code
     */
    Integer currencyCode;

    public String getUserCriteria() {
        return userCriteria;
    }

    public void setUserCriteria(String userCriteria) {
        this.userCriteria = userCriteria;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Integer currencyCode) {
        this.currencyCode = currencyCode;
    }
}
