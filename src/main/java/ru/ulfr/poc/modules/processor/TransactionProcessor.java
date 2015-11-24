package ru.ulfr.poc.modules.processor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for transaction processor
 */
public interface TransactionProcessor {
    @Async
    @Transactional
    void processTransaction(long transactionId);
}
