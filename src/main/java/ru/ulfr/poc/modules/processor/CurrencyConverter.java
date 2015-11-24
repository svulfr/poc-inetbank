package ru.ulfr.poc.modules.processor;

import java.math.BigDecimal;

/**
 * Interface for Currency Converter
 */
public interface CurrencyConverter {
    BigDecimal getConversionRate(int from, int to);
}
