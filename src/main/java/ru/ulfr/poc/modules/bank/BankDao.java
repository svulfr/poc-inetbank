package ru.ulfr.poc.modules.bank;

import ru.ulfr.poc.modules.bank.model.Currency;

import java.util.List;

/**
 * Interface for Bank Data Access Object bean
 */
public interface BankDao {

    Currency getCurrency(int code);

    List<Currency> listCurrencies();
}
