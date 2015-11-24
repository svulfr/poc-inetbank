package ru.ulfr.poc.modules.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.ulfr.poc.Config;
import ru.ulfr.poc.modules.users.UserDao;
import ru.ulfr.poc.modules.users.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Root controller providing basic methods, potentially needed by any controller in the application
 */
public class AbstractController {

    protected Logger logger = LogManager.getLogger(getClass());

    @Autowired
    UserDao userDao;

    /**
     * Returns current session user based on SecurityContext.
     *
     * @return {@link User} object of current user or null
     */
    protected User getSessionUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;

            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            Object systemUser = attributes.getAttribute("currentUser", RequestAttributes.SCOPE_SESSION);
            if (systemUser == null) {
                systemUser = userDao.getUserByLogin(user.getUsername());
                attributes.setAttribute("currentUser", systemUser, RequestAttributes.SCOPE_SESSION);
            }
            return (User) systemUser;
        } else {
            return null;
        }
    }

    /**
     * Performs check that user has role specified. Technically needed to reduce code size in controllers
     *
     * @param role required role
     */
    protected void assertPrivileges(String role) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        if (!request.isUserInRole(role)) {
            throw new HTTP403Exception();
        }
    }

    /**
     * Performs check that user is the one logged on
     *
     * @param userId user id required
     */
    protected void assertUser(long userId) {
        if (getSessionUser().getId() != userId) {
            throw new HTTP403Exception();
        }
    }

    /**
     * Performs check that user is the one logged on
     *
     * @param userId user id required
     */
    protected void assertUserOrAdmin(long userId) {
        if (getSessionUser().getId() != userId) {
            assertPrivileges(Config.ROLE_ADMIN);
        }
    }
}
