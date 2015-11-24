package ru.ulfr.poc.modules.bank.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Currency pair working as key for {@link CurrencyExchangeRate}
 * Values are ISO 4217 numeric codes for currencies
 */
@Embeddable
@Table(name = "currencies_exchange")
@SuppressWarnings("unused")
public class CurrencyExchangeRateKey implements Serializable {

    /**
     * ISO 4217 code for source currency
     */
    @Column(name = "code_from")
    private int codeFrom;

    /**
     * ISO 4217 code for target currency
     */
    @Column(name = "code_to")
    private int codeTo;

    public CurrencyExchangeRateKey() {
    }

    public CurrencyExchangeRateKey(int codeFrom, int codeTo) {
        this.codeFrom = codeFrom;
        this.codeTo = codeTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyExchangeRateKey that = (CurrencyExchangeRateKey) o;

        return codeFrom == that.codeFrom && codeTo == that.codeTo;

    }

    @Override
    public int hashCode() {
        int result = codeFrom;
        result = 31 * result + codeTo;
        return result;
    }

    public int getCodeFrom() {
        return codeFrom;
    }

    public void setCodeFrom(int codeFrom) {
        this.codeFrom = codeFrom;
    }

    public int getCodeTo() {
        return codeTo;
    }

    public void setCodeTo(int codeTo) {
        this.codeTo = codeTo;
    }
}
