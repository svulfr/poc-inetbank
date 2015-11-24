package ru.ulfr.poc.modules.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ulfr.poc.Config;
import ru.ulfr.poc.modules.users.model.User;
import ru.ulfr.poc.modules.utils.AbstractController;

import java.util.List;

/**
 * REST Controller for user manipulation
 */
@RestController
@RequestMapping(path = "/rest/user")
@SuppressWarnings("unused")
public class UserRest extends AbstractController {

    @Autowired
    UserDao userDao;

    /**
     * Creates user
     */
    @RequestMapping(path = "/", method = RequestMethod.POST)
    public User createUser(@RequestBody User user) {
        assertPrivileges(Config.ROLE_ADMIN);
        user = userDao.createUser(user);
        return user;
    }

    /**
     * List users
     */
    @RequestMapping(path = "/search/{criteria}", method = RequestMethod.GET)
    public List<User> searchUsers(@PathVariable String criteria) {
        assertPrivileges(Config.ROLE_ADMIN);
        return userDao.search(criteria);
    }


}
