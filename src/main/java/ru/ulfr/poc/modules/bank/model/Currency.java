package ru.ulfr.poc.modules.bank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model object representing currency
 */
@Entity
@Table(name = "currencies")
@SuppressWarnings("unused")
public class Currency implements Serializable {

    /**
     * ISO 4217 numeric code for currency
     */
    @Id
    @Column(name = "code")
    private int code;

    /**
     * Currency name
     */
    @Column(name = "name")
    private String name;

    /**
     * ISO 4217 text code
     */
    @Column(name = "ui_code")
    private String uiCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        return code == currency.code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUiCode() {
        return uiCode;
    }

    public void setUiCode(String uiCode) {
        this.uiCode = uiCode;
    }
}
