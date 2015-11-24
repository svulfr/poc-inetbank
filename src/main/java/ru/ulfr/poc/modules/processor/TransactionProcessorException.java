package ru.ulfr.poc.modules.processor;

/**
 * Exception used internally in transaction processor to handle any error
 * with transaction processing
 */
class TransactionProcessorException extends Exception {
    int code;

    public TransactionProcessorException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
