package ru.ulfr.poc.modules.bank.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model object for Currency exchange
 * <p/>
 * Uses ISO 4217 codes to reference currency code number
 */
@Entity
@Table(name = "currencies_exchange")
@SuppressWarnings("unused")
public class CurrencyExchangeRate implements Serializable {

    /**
     * Key for currency exchange rate
     */
    @EmbeddedId
    private CurrencyExchangeRateKey id;

    /**
     * Rate in form target_amount = source_amount * rate
     */
    @Column(name = "rate")
    private double rate;

    public CurrencyExchangeRateKey getId() {
        return id;
    }

    public void setId(CurrencyExchangeRateKey id) {
        this.id = id;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
