package ru.ulfr.poc.modules.utils;

import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.ulfr.poc.Config;

import javax.servlet.http.HttpServletRequest;

/**
 * Abstract class with methods needed by page controllers
 */
public class AbstractPageController extends AbstractController {

    /**
     * Interface for {@link #safe(PageFiller)} method
     */
    public interface PageFiller {
        String fillPage();
    }

    protected void injectUserInfo(Model model) {
        model.addAttribute("user", getSessionUser());
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        model.addAttribute("userAdmin", request.isUserInRole(Config.ROLE_ADMIN));
        model.addAttribute("userCustomer", request.isUserInRole(Config.ROLE_USER));
    }

    /**
     * Lambda wrapper for producing controller results.
     * Catches exceptions
     *
     * @param filler code that provides
     * @return view id
     */
    protected String safe(PageFiller filler) {
        try {
            return filler.fillPage();
        } catch (HTTP500Exception x) {
            return "err-internal-error";
        } catch (HTTP422Exception x) {
            return "err-invalid-arg";
        } catch (HTTP404Exception x) {
            return "err-not-found";
        } catch (HTTP403Exception x) {
            return "err-forbidden";
        }
    }
}
