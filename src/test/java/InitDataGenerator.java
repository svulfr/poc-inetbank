import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.ulfr.poc.Config;
import ru.ulfr.poc.MvcResourcesConfig;
import ru.ulfr.poc.PersistenceContext;
import ru.ulfr.poc.modules.account.AccountDao;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.atm.AtmDao;
import ru.ulfr.poc.modules.atm.model.DepositRequest;
import ru.ulfr.poc.modules.atm.model.WithdrawRequest;
import ru.ulfr.poc.modules.bank.BankDao;
import ru.ulfr.poc.modules.bank.model.Currency;
import ru.ulfr.poc.modules.bank.model.TransferOrder;
import ru.ulfr.poc.modules.processor.TransactionProcessor;
import ru.ulfr.poc.modules.users.UserDao;
import ru.ulfr.poc.modules.users.UserRest;
import ru.ulfr.poc.modules.users.model.User;
import ru.ulfr.poc.modules.users.model.UserRole;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MvcResourcesConfig.class, PersistenceContext.class})
@WebAppConfiguration
@Rollback(value = false)
public class InitDataGenerator {

    private Logger logger = LogManager.getLogger(getClass());

    private MockMvc mockMvc;

    @Autowired
    private UserRest userRest;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BankDao bankDao;

    @Autowired
    private AtmDao atmDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Autowired
    private WebApplicationContext context;

    @javax.persistence.PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    private long randomLong(long min, long max) {
        double limit = max - min + 0.999999;
        return min + new Double(Math.floor(limit * Math.random())).longValue();
    }

    private int randomInt(int min, int max) {
        double limit = max - min + 0.999999;
        return min + new Double(Math.floor(limit * Math.random())).intValue();
    }

    private HttpSession authorizeAdmin() throws Throwable {
        HttpSession session = mockMvc.perform(post("/login").param("username", "user1").param("password", "user1"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/"))
                .andReturn()
                .getRequest()
                .getSession();

        Assert.assertNotNull(session);
        return session;
    }

    /**
     * Generates set of accounts: admin and 100 users.
     * For each user account is generated for each currency registered in the system
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void generateAccounts() throws Exception {

        User user = new User();
        user.setUserName("admin");
        user.setPassword("admin");
        user.setName("System Administrator");
        user.setEmail("admin@aaa.bb");
        user = userDao.createUser(user);
        UserRole role = new UserRole(user.getUserName(), Config.ROLE_ADMIN);
        em.persist(role);


        for (int i = 0; i < 100; i++) {
            user = new User();
            user.setUserName(String.format("user%04d", i));
            user.setPassword(String.format("pass%04d", i));
            user.setName(String.format("user-name-%04d", i));
            user.setEmail(String.format("%04d@user-email", i));
            userDao.createUser(user);

            for (Currency c : bankDao.listCurrencies()) {
                Account acc = new Account();
                acc.setCurrency(bankDao.getCurrency(c.getCode()));
                accountDao.createAccount(user.getId(), acc);
            }
        }

        em.createQuery("update Account set amount = 1000")
                .executeUpdate();

    }

    /**
     * Generates 15000 test transactions for random accounts.
     * 5 threads generate 1000 sequences of triplet of transfer deposit and withdrawal.
     *
     * @throws Exception
     */
    @Test
    public void performTransactions() throws Exception {
        final List<Currency> currencies = bankDao.listCurrencies();
        logger.info("start test");
        final Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                // transfer
                Account accountS = em.find(Account.class, randomLong(1, 300));
                Account accountT = em.find(Account.class, randomLong(1, 300));
                BigDecimal amount = BigDecimal.valueOf(Math.random() * 1000);
                Currency currency = currencies.get(randomInt(0, currencies.size() - 1));
                long tId = accountDao.createTransaction(accountS.getId(),
                        new TransferOrder(accountT.getId(), amount, currency.getCode()));
                transactionProcessor.processTransaction(tId);

                // deposit
                accountT = em.find(Account.class, randomLong(1, 300));
                amount = BigDecimal.valueOf(Math.random() * 1000);
                currency = currencies.get(randomInt(0, currencies.size() - 1));
                tId = atmDao.deposit(new DepositRequest(accountT.getId(), amount, currency.getCode()));
                if (tId > 0) {
                    transactionProcessor.processTransaction(tId);
                }

                // withdraw
                accountS = em.find(Account.class, randomLong(1, 300));
                amount = BigDecimal.valueOf(Math.random() * 1000);
                currency = currencies.get(randomInt(0, currencies.size() - 1));
                tId = atmDao.withdraw(new WithdrawRequest(accountS.getId(), amount, currency.getCode()));
                if (tId > 0) {
                    transactionProcessor.processTransaction(tId);
                }
            }
        };
        for (int tc = 0; tc < 5; tc++) {
            new Thread(task).start();
        }

        // since tx data generation starts asynchronous tasks of transaction processing, which are out of control,
        // such an approach is used for development speed. Alternatively code will depend on tests which is much
        // worse
        Thread.sleep(30000);

        logger.info("end test");
    }
}
