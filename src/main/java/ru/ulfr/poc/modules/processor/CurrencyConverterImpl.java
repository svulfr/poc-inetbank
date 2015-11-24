package ru.ulfr.poc.modules.processor;

import org.springframework.stereotype.Service;
import ru.ulfr.poc.modules.bank.model.CurrencyExchangeRate;
import ru.ulfr.poc.modules.bank.model.CurrencyExchangeRateKey;
import ru.ulfr.poc.modules.utils.AbstractDao;

import java.math.BigDecimal;

/**
 * Implementation for currency conversion service
 */
@Service
public class CurrencyConverterImpl extends AbstractDao implements CurrencyConverter {

    /**
     * Returns conversion rate for currencies
     *
     * @param from currency to convert from
     * @param to   currency to convert to
     * @return conversion rate
     */
    @Override
    public BigDecimal getConversionRate(int from, int to) {
        if (from == to) {
            return BigDecimal.ONE;
        } else {
            CurrencyExchangeRate rate = em.find(CurrencyExchangeRate.class, new CurrencyExchangeRateKey(from, to));
            return BigDecimal.valueOf(rate.getRate());
        }
    }
}
