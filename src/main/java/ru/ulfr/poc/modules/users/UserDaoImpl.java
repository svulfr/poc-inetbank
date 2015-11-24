package ru.ulfr.poc.modules.users;

import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.Config;
import ru.ulfr.poc.modules.users.model.User;
import ru.ulfr.poc.modules.users.model.UserRole;
import ru.ulfr.poc.modules.utils.AbstractDao;

import java.util.List;

/**
 * Data access and business logic bean for user management
 */
@Repository
public class UserDaoImpl extends AbstractDao implements UserDao {

    /**
     * Fetches user for specified id
     *
     * @param userId user id to fetch
     * @return User object, null if user not found
     */
    @Override
    public User getUser(long userId) {
        return em.find(User.class, userId);
    }

    /**
     * Perform lookup of account by login name.
     *
     * @param login login name
     * @return {@link User} object if there is matching account, null otherwise
     */
    @Override
    public User getUserByLogin(String login) {
        List<User> accounts = em.createQuery("from User where userName = ?1", User.class)
                .setParameter(1, login)
                .getResultList();
        return accounts.size() > 0 ? accounts.get(0) : null;
    }

    /**
     * Fetches list of accounts from DB
     *
     * @return list of {@link User} objects
     */
    @Override
    public List<User> list() {
        return em.createQuery("from User", User.class).getResultList();
    }

    /**
     * Updates fields of account. Non-null field will be applied to the Database
     *
     * @param user account with fields to update set. Null fields will be ignored
     * @return updated object
     */
    @Override
    @Transactional
    public User updateUser(User user) {
        User existing = em.find(User.class, user.getId());
        // enumerate specified fields. May be implemented as customized bean copy procedure
        if (user.getUserName() != null) {
            existing.setUserName(user.getUserName());
        }
        if (user.getPassword() != null) {
            existing.setPassword(new StandardPasswordEncoder(Config.PASSWORD_ENCODER_SECRET).encode(user.getPassword()));
        }
        // record will be updated when closing transaction.
        return existing;
    }

    /**
     * Creates user in the database and assigns role
     *
     * @param user user to create
     * @return user with id assigned
     */
    @Override
    @Transactional
    public User createUser(User user) {
        user.setPassword(new StandardPasswordEncoder(Config.PASSWORD_ENCODER_SECRET).encode(user.getPassword()));
        em.persist(user);
        UserRole role = new UserRole(user.getUserName(), Config.ROLE_USER);
        em.persist(role);
        return user;
    }

    /**
     * Performs search users by username or e-mail part specified by criteria
     *
     * @param criteria username or e-mail part
     * @return list of users
     */
    @Override
    public List<User> search(String criteria) {
        return em.createQuery("from User where name like ?1 or email like ?1", User.class)
                .setParameter(1, String.format("%%%s%%", criteria))
                .getResultList();
    }
}
