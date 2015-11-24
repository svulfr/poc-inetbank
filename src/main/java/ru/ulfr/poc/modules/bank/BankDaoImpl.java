package ru.ulfr.poc.modules.bank;

import org.springframework.stereotype.Repository;
import ru.ulfr.poc.modules.bank.model.Currency;
import ru.ulfr.poc.modules.utils.AbstractDao;

import java.util.List;

/**
 * Implementation for bank Data Access Object
 * <p/>
 * Manages account to account transfers and currency information
 */
@Repository
public class BankDaoImpl extends AbstractDao implements BankDao {


    /**
     * Returns currency by currency code
     *
     * @param code currency code
     * @return {@link Currency} representing requested currency
     */
    @Override
    public Currency getCurrency(int code) {
        return em.find(Currency.class, code);
    }

    /**
     * Returns list of supported currencies
     *
     * @return list of currencies
     */
    @Override
    public List<Currency> listCurrencies() {
        return em.createQuery("from Currency", Currency.class)
                .getResultList();
    }
}
