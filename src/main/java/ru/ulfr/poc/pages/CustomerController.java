package ru.ulfr.poc.pages;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ulfr.poc.modules.account.AccountRest;
import ru.ulfr.poc.modules.bank.BankRest;
import ru.ulfr.poc.modules.bank.model.Currency;
import ru.ulfr.poc.modules.processor.model.Transaction;
import ru.ulfr.poc.modules.users.model.User;
import ru.ulfr.poc.modules.utils.AbstractPageController;
import ru.ulfr.poc.modules.utils.HTTP403Exception;
import ru.ulfr.poc.modules.utils.HTTP500Exception;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Page controller for serving customer user space
 * <p/>
 * Entire controller access is restricted to ROLE_USER with {@link PreAuthorize} annotation
 * <p/>
 * Though since we differ allowed actions to particular users, DAO is not accessed, access is done via REST
 * controllers implementing all security restrictions
 */
@Controller
@RequestMapping(path = "/customer")
@PreAuthorize("hasRole('ROLE_USER')")
@SuppressWarnings("unused")
public class CustomerController extends AbstractPageController {

    @Autowired
    BankRest bankRest;

    @Autowired
    AccountRest accountRest;

    @RequestMapping(path = "/")
    public String home(Model model) {
        injectUserInfo(model);
        model.addAttribute("accounts", accountRest.listAccounts());
        return "customer";
    }

    private void injectDepositWithdraw(User user, long accountId, Model model) {
        injectUserInfo(model);
        model.addAttribute("account", accountRest.getAccount(accountId));
        model.addAttribute("currencies", bankRest.listCurrencies());
    }

    @RequestMapping(path = "/{accountId}/deposit")
    public String deposit(@PathVariable long accountId,
                          Model model) {
        return safe(() -> {
            injectDepositWithdraw(getSessionUser(), accountId, model);
            return "customer-deposit";
        });
    }

    @RequestMapping(path = "/{accountId}/withdraw")
    public String withdraw(@PathVariable long accountId,
                           Model model) {
        return safe(() -> {
            injectDepositWithdraw(getSessionUser(), accountId, model);
            return "customer-withdraw";
        });
    }

    @RequestMapping(path = "/{accountId}/log")
    public String log(@PathVariable long accountId,
                      Model model) {
        return safe(() -> {
            injectUserInfo(model);
            Map<String, Currency> currencies = bankRest.listCurrencies()
                    .stream().collect(Collectors.toMap(c -> "" + c.getCode(), Function.identity()));
            model.addAttribute("user", getSessionUser());
            model.addAttribute("account", accountRest.getAccount(accountId));
            model.addAttribute("transactions", accountRest.listTransactions(accountId));
            model.addAttribute("currencies", currencies);
            return "customer-log";
        });
    }

    @RequestMapping(path = "/statement")
    public String statement(@RequestParam(name = "account") long accId,
                            @RequestParam(name = "tx") long txId,
                            Model model) {
        return safe(() -> {
            injectUserInfo(model);
            Transaction tx = accountRest.getTransaction(accId, txId);
            model.addAttribute("transaction", tx);
            model.addAttribute("account", accountRest.getAccount(accId));
            Map<String, Currency> currencies = bankRest.listCurrencies()
                    .stream().collect(Collectors.toMap(c -> "" + c.getCode(), Function.identity()));
            model.addAttribute("currencies", currencies);
            return "customer-statement";
        });
    }

    @RequestMapping(path = "/statement-pdf")
    public ResponseEntity<byte[]> getStatementPDF(@RequestParam(name = "account") long accId,
                                                  @RequestParam(name = "tx") long txId) {
        Model model = new ExtendedModelMap();
        String response = this.statement(accId, txId, model);
        if (!response.startsWith("err-")) {
            try {
                byte[] data = new StatementPdfRenderer(model).getPDF();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("application/pdf"));
                String filename = String.format("statement-%d.pdf", txId);
                headers.setContentDispositionFormData(filename, filename);
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                return new ResponseEntity<>(data, headers, HttpStatus.OK);
            } catch (IOException | COSVisitorException x) {
                logger.warn("error creating PDF: " + x.toString());
                throw new HTTP500Exception();
            }
        } else {
            logger.warn("error accessing transaction log record (forbidden)");
            throw new HTTP403Exception();
        }
    }
}
