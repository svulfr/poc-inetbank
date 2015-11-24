package ru.ulfr.poc.modules.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Object used for accepting parameters from client
 */
@SuppressWarnings("unused")
public class TransferOrder implements Serializable {
    /**
     * Recipient account Id
     */
    private long recipient;

    /**
     * Amount to transfer to recipient
     */
    private BigDecimal amount;

    /**
     * Currency ISO 4217 code
     */
    private int currency;

    public TransferOrder() {
    }

    public TransferOrder(long recipient, BigDecimal amount, int currency) {
        this.recipient = recipient;
        this.amount = amount;
        this.currency = currency;
    }

    public long getRecipient() {
        return recipient;
    }

    public void setRecipient(long recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }
}
