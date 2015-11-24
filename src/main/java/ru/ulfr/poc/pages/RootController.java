package ru.ulfr.poc.pages;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ulfr.poc.Config;
import ru.ulfr.poc.modules.utils.AbstractPageController;

import javax.servlet.http.HttpServletRequest;


/**
 * Page controller for web pages
 */
@Controller
@RequestMapping(path = "/")
@SuppressWarnings("unused")
public class RootController extends AbstractPageController {

    @RequestMapping(path = "/")
    public String index(Model model, HttpServletRequest request) {
        return safe(() -> {
            injectUserInfo(model);
            if (request.isUserInRole(Config.ROLE_ADMIN)) {
                model.asMap().clear();
                return "redirect:/admin/";
            } else if (request.isUserInRole(Config.ROLE_USER)) {
                model.asMap().clear();
                return "redirect:/customer/";
            }
            return "index";
        });
    }

    @RequestMapping(path = "/login")
    public String login(Model model) {
        return safe(() -> {
            injectUserInfo(model);
            return "login";
        });
    }
}
