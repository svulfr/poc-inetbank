package ru.ulfr.poc.modules.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.processor.model.Transaction;
import ru.ulfr.poc.modules.utils.AbstractDao;

import java.math.BigDecimal;

/**
 * Service that implements transaction processing
 * <p/>
 * This service should normally be implemented as MDB,
 * but in order to simplify structure we stay at asynchronous method calls
 */
@Service
public class TransactionProcessorImpl extends AbstractDao implements TransactionProcessor {

    @Autowired
    CurrencyConverter currencyConverter;

    /**
     * Performs operation of transfer between accounts
     *
     * @param tx Transaction to process
     */
    private void transfer(Transaction tx) {
        try {
            // fetch source and target accounts
            Account source = em.find(Account.class, tx.getOriginId());
            Account target = em.find(Account.class, tx.getRecipientId());

            // evaluate amounts to transfer
            BigDecimal rateSrc = currencyConverter.getConversionRate(source.getCurrency().getCode(), tx.getTxCurrency());
            BigDecimal rateTgt = currencyConverter.getConversionRate(tx.getTxCurrency(), target.getCurrency().getCode());
            BigDecimal amountSrc = tx.getTxAmount().divide(rateSrc, BigDecimal.ROUND_CEILING);
            BigDecimal amountTgt = tx.getTxAmount().multiply(rateTgt);

            // apply calculated amounts
            tx.setOriginAmount(amountSrc);
            tx.setRecipientAmount(amountTgt);

            // check that source account has sufficient funds
            if (source.getAmount().compareTo(amountSrc) < 0) {
                throw new TransactionProcessorException(Transaction.DECLINE_REASON_INSUFFICIENT_FUNDS);
            }

            // perform transfer
            source.setAmount(source.getAmount().subtract(amountSrc));
            target.setAmount(target.getAmount().add(amountTgt));

            // update transaction state
            tx.setState(Transaction.STATE_CONFIRMED);

            // send messages on successful transfer (out of scope of test task)
            logger.info(String.format("transaction done: %d: %.2f %s -> %d: %.2f %s",
                    source.getId(), amountSrc, source.getCurrency().getUiCode(),
                    target.getId(), amountTgt, target.getCurrency().getUiCode()));
        } catch (TransactionProcessorException x) {
            logger.info("transaction processing failed with code " + x.getCode());
            tx.setState(Transaction.STATE_DECLINED + x.getCode());
        }
    }

    /**
     * Performs operation of deposit
     *
     * @param tx transaction to process
     */
    private void deposit(Transaction tx) {
        // fetch account
        Account target = em.find(Account.class, tx.getRecipientId());

        // evaluate amount to transfer
        BigDecimal rateTgt = currencyConverter.getConversionRate(tx.getTxCurrency(), target.getCurrency().getCode());
        BigDecimal amountTgt = tx.getTxAmount().multiply(rateTgt);

        // apply calculated amount
        tx.setRecipientAmount(amountTgt);

        // update balance
        target.setAmount(target.getAmount().add(amountTgt));

        // update transaction state
        tx.setState(Transaction.STATE_CONFIRMED);

        // send messages on successful transfer (out of scope of test task)
        logger.info(String.format("transaction done: deposit -> %d: %.2f %s",
                target.getId(), amountTgt, target.getCurrency().getUiCode()));
    }

    /**
     * Performs operation of withdraw
     *
     * @param tx transaction to process
     */
    private void withdraw(Transaction tx) {
        try {
            // fetch account
            Account source = em.find(Account.class, tx.getOriginId());

            // evaluate amount to transfer
            BigDecimal rateSrc = currencyConverter.getConversionRate(source.getCurrency().getCode(), tx.getTxCurrency());
            BigDecimal amountSrc = tx.getTxAmount().divide(rateSrc, BigDecimal.ROUND_CEILING);

            // apply calculated amount
            tx.setOriginAmount(amountSrc);

            // check that source account has sufficient funds
            if (source.getAmount().compareTo(amountSrc) < 0) {
                throw new TransactionProcessorException(Transaction.DECLINE_REASON_INSUFFICIENT_FUNDS);
            }

            // update balance
            source.setAmount(source.getAmount().subtract(amountSrc));

            // update transaction state
            tx.setState(Transaction.STATE_CONFIRMED);

            // send messages on successful transfer (out of scope of test task)
            logger.info(String.format("transaction done: %d: %.2f %s -> withdraw",
                    source.getId(), amountSrc, source.getCurrency().getUiCode()));
        } catch (TransactionProcessorException x) {
            logger.warn(String.format("transaction processing failed with code %d", x.getCode()));
            tx.setState(Transaction.STATE_DECLINED + x.getCode());
        }
    }

    /**
     * Performs transaction on transferring amount from one account to another
     *
     * @param transactionId transaction to process. Typically parameter from message
     */
    @Override
    @Async
    @Transactional
    public void processTransaction(long transactionId) {
        // fetch transaction
        Transaction transaction = em.find(Transaction.class, transactionId);
        if (transaction == null) {
            // transaction not found, impossible case
            logger.error(String.format("transaction id=%d not found", transactionId));
            return;
        }
        switch (transaction.getTxType()) {
            case Transaction.TYPE_TRANSFER:
                this.transfer(transaction);
                break;
            case Transaction.TYPE_DEPOSIT:
                this.deposit(transaction);
                break;
            case Transaction.TYPE_WITHDRAW:
                this.withdraw(transaction);
                break;
            default:
                transaction.setState(Transaction.STATE_DECLINED + Transaction.DECLINE_REASON_UNKNOWN_TYPE);
        }
    }
}
