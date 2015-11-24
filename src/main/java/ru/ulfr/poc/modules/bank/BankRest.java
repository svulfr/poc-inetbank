package ru.ulfr.poc.modules.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.ulfr.poc.modules.bank.model.Currency;
import ru.ulfr.poc.modules.utils.AbstractController;

import java.util.List;

/**
 * REST Controller for bank accounts manipulation
 */
@RestController
@RequestMapping(path = "/rest/bank")
@SuppressWarnings("unused")
public class BankRest extends AbstractController {

    @Autowired
    BankDao bankDao;

    /**
     * Returns list of supported currencies
     *
     * @return list of currencies
     */
    @RequestMapping(path = "/currencies", method = RequestMethod.GET)
    public List<Currency> listCurrencies() {
        return bankDao.listCurrencies();
    }

}
