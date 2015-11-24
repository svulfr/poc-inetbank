package ru.ulfr.poc.modules.users;

import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.modules.users.model.User;

import java.util.List;

/**
 * interface for user management DAO
 */
@SuppressWarnings("unused")
public interface UserDao {
    User getUser(long userId);

    User getUserByLogin(String login);

    List<User> list();

    @Transactional
    User updateUser(User user);

    @Transactional
    User createUser(User user);

    List<User> search(String criteria);
}
