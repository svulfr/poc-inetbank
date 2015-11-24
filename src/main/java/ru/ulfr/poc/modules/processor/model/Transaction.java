package ru.ulfr.poc.modules.processor.model;

import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.users.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model Object representing transaction record
 */
@Entity
@Table(name = "accounts_tx")
@SuppressWarnings("unused")
public class Transaction implements Serializable {

    /**
     * Transaction was created and passed for processing, exact status pending
     */
    public static final int STATE_INITIATED = -1;

    /**
     * Transaction was confirmed, i.e. successfully executed
     */
    public static final int STATE_CONFIRMED = 0;

    /**
     * Transaction was declined
     */
    public static final int STATE_DECLINED = 1;

    /**
     * Unable to process transaction because of insufficient funds
     */
    public static final int DECLINE_REASON_INSUFFICIENT_FUNDS = 0;

    /**
     * Unable to process transaction because currency is not known to the system
     */
    public static final int DECLINE_REASON_UNKNOWN_CURRENCY = 1;

    /**
     * Unable to process transaction because account is not found
     */
    public static final int DECLINE_REASON_UNKNOWN_PARTY = 2;

    /**
     * Unknown transaction type
     */
    public static final int DECLINE_REASON_UNKNOWN_TYPE = 3;

    /**
     *
     */
    public static final int TYPE_TRANSFER = 0;
    public static final int TYPE_WITHDRAW = 1;
    public static final int TYPE_DEPOSIT = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * type for transaction
     */
    @Column(name = "tx_type")
    private int txType;

    /**
     * Requested amount to transfer
     */
    @Column(name = "tx_amount")
    private BigDecimal txAmount;

    /**
     * ISO 4217 N currency code for requested amount to transfer
     */
    @Column(name = "tx_currency")
    private int txCurrency;

    /**
     * Sender account number
     */
    @Column(name = "orig")
    private Long originId;

    /**
     * Sender amount in currency
     */
    @Column(name = "orig_amount")
    private BigDecimal originAmount;

    /**
     * Sender currency
     * This can be evaluated from {@link Account}, but we denormalize DB
     * in order to increase performance
     */
    @Column(name = "orig_currency")
    private int originCurrency;

    /**
     * Recipient account number
     */
    @Column(name = "rcpt")
    private Long recipientId;

    /**
     * Recipient amount in recipient currency
     */
    @Column(name = "rcpt_amount")
    private BigDecimal recipientAmount;

    /**
     * Recipient currency
     * This can be evaluated from {@link Account}, but we denormalize DB
     * in order to increase performance
     */
    @Column(name = "rcpt_currency")
    private int recipientCurrency;

    /**
     * Date when transaction was initiated
     */
    @Column(name = "tx_date", insertable = false, updatable = false)
    private Date createdOn;

    /**
     * Used when returning statement
     */
    @Transient
    private User party;

    /**
     * Represents transaction state,
     * Value is negative for initiated transaction ({@link Transaction#STATE_INITIATED}
     * <p/>
     * 0 for successful transaction ({@link Transaction#STATE_CONFIRMED}
     * <p/>
     * other for for failed transaction (evaluated as {@link Transaction#STATE_DECLINED} + reason,
     * which is one of
     * <p/>
     * {@link Transaction#DECLINE_REASON_INSUFFICIENT_FUNDS}
     * {@link Transaction#DECLINE_REASON_UNKNOWN_CURRENCY}
     */
    @Column(name = "state")
    private int state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTxType() {
        return txType;
    }

    public void setTxType(int txType) {
        this.txType = txType;
    }

    public BigDecimal getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(BigDecimal txAmount) {
        this.txAmount = txAmount;
    }

    public int getTxCurrency() {
        return txCurrency;
    }

    public void setTxCurrency(int txCurrency) {
        this.txCurrency = txCurrency;
    }

    public Long getOriginId() {
        return originId;
    }

    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    public BigDecimal getOriginAmount() {
        return originAmount;
    }

    public void setOriginAmount(BigDecimal originAmount) {
        this.originAmount = originAmount;
    }

    public int getOriginCurrency() {
        return originCurrency;
    }

    public void setOriginCurrency(int originCurrency) {
        this.originCurrency = originCurrency;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public BigDecimal getRecipientAmount() {
        return recipientAmount;
    }

    public void setRecipientAmount(BigDecimal recipientAmount) {
        this.recipientAmount = recipientAmount;
    }

    public int getRecipientCurrency() {
        return recipientCurrency;
    }

    public void setRecipientCurrency(int recipientCurrency) {
        this.recipientCurrency = recipientCurrency;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public User getParty() {
        return party;
    }

    public void setParty(User party) {
        this.party = party;
    }
}
