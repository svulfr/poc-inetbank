package ru.ulfr.poc.modules.account.model;

import ru.ulfr.poc.modules.bank.model.Currency;
import ru.ulfr.poc.modules.users.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Model object representing bank account
 */
@Entity
@Table(name = "accounts")
@SuppressWarnings("unused")
public class Account implements Serializable {
    /**
     * Unique internal id of this bank account. Any standard application like ISO 13616 should be evaluated
     * on top of this ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * References user, owner of this account
     */
    @JoinColumn(name = "user")
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    /**
     * Represents amount in this bank account
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * Represents currency for this bank account
     */
    @JoinColumn(name = "currency")
    @ManyToOne(fetch = FetchType.EAGER)
    private Currency currency;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
