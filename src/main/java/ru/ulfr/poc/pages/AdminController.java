package ru.ulfr.poc.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ulfr.poc.modules.account.AccountDao;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.bank.BankDao;
import ru.ulfr.poc.modules.users.UserDao;
import ru.ulfr.poc.modules.utils.AbstractPageController;

/**
 * Page controller to serve admin user space
 * <p/>
 * Entire controller access is restricted to admins using annotation, which allows direct access to DAO objects
 * <p/>
 * javaDoc annotations in the code is omitted for better readability of request mapping paths.
 */
@Controller
@RequestMapping(path = "/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@SuppressWarnings("unused")
public class AdminController extends AbstractPageController {

    @Autowired
    BankDao bankDao;

    @Autowired
    AccountDao accountDao;

    @Autowired
    UserDao userDao;

    @RequestMapping(path = "/")
    public String home(Model model) {
        return safe(() -> {
            injectUserInfo(model);
            return "admin";
        });
    }

    @RequestMapping(path = "/customers")
    public String customers(Model model) {
        return safe(() -> {
            injectUserInfo(model);
            return "admin-customers";
        });
    }

    @RequestMapping(path = "/customers/details")
    public String customerDetails(@RequestParam(name = "id") long userId,
                                  Model model) {
        return safe(() -> {
            injectUserInfo(model);
            model.addAttribute("customer", userDao.getUser(userId));
            model.addAttribute("accounts", accountDao.listAccounts(userId));
            return "admin-customer-details";
        });
    }

    @RequestMapping(path = "/accounts")
    public String accounts(Model model) {
        return safe(() -> {
            injectUserInfo(model);
            model.addAttribute("currencies", bankDao.listCurrencies());
            return "admin-accounts";
        });
    }

    @RequestMapping(path = "/accounts/details")
    public String accountDetails(@RequestParam(name = "id") long accountId,
                                 Model model) {
        return safe(() -> {
            injectUserInfo(model);
            Account account = accountDao.getAccount(accountId);
            model.addAttribute("customer", account.getUser());
            model.addAttribute("accounts", accountDao.listAccounts(account.getUser().getId()));
            model.addAttribute("accountId", accountId);
            return "admin-account-details";
        });
    }

    @RequestMapping(path = "/transactions")
    public String transactions(@RequestParam(name = "id") long accountId,
                               Model model) {
        return safe(() -> {
            injectUserInfo(model);
            model.addAttribute("account", accountDao.getAccount(accountId));
            return "admin-transactions";
        });
    }
}
