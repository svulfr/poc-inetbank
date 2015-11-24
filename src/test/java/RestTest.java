import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.ulfr.poc.MvcResourcesConfig;
import ru.ulfr.poc.PersistenceContext;
import ru.ulfr.poc.SecurityConfig;
import ru.ulfr.poc.modules.account.AccountDao;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.account.model.AccountSearchCriteria;
import ru.ulfr.poc.modules.bank.model.TransferOrder;
import ru.ulfr.poc.modules.processor.model.Transaction;
import ru.ulfr.poc.modules.users.model.User;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MvcResourcesConfig.class, PersistenceContext.class, SecurityConfig.class})
@WebAppConfiguration
@Rollback(value = false)
public class RestTest {

    /**
     * Spring MVC mock
     */
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    AccountDao accountDao;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @javax.persistence.PersistenceContext
    private EntityManager em;

    private Account randomAccount() {
        return em.createQuery("from Account order by rand()", Account.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    private Account randomAccountNot(Long... id) {
        return em.createQuery("from Account where id not in (?1) order by rand()", Account.class)
                .setParameter(1, Arrays.asList(id))
                .setMaxResults(1)
                .getSingleResult();
    }

    private Account randomAccountNotUser(Long... id) {
        return em.createQuery("from Account where user.id not in (?1) order by rand()", Account.class)
                .setParameter(1, Arrays.asList(id))
                .setMaxResults(1)
                .getSingleResult();
    }

    private MockHttpSession login(User user) throws Exception {
        String name = user.getUserName();
        String pass = name.replace("user", "pass");
        MockHttpSession session = (MockHttpSession) mockMvc
                .perform(post("/login")
                        .param("username", name)
                        .param("password", pass))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/"))
                .andReturn()
                .getRequest()
                .getSession();

        Assert.assertNotNull(session);
        return session;
    }

    private MockHttpSession loginAdmin() throws Exception {
        User admin = new User();
        admin.setUserName("admin");
        admin.setPassword("admin");
        return login(admin);
    }

    /**
     * Test for searching accounts by admin user
     *
     * @throws Exception
     */
    @Test
    public void testSearchAccounts() throws Exception {
        AccountSearchCriteria criteria = new AccountSearchCriteria();
        criteria.setUserCriteria("0005");
        criteria.setCurrencyCode(840);
        List<Account> result = accountDao.search(criteria);

        MockHttpSession session = loginAdmin();
        mockMvc.perform(post("/rest/account/search")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.toJSON(criteria))
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(result.size())));
    }

    /**
     * Test for searching account by regular user (forbidden)
     *
     * @throws Exception
     */
    @Test
    public void testSearchAccountsNoAdmin() throws Exception {
        AccountSearchCriteria criteria = new AccountSearchCriteria();
        criteria.setUserCriteria("0005");
        criteria.setCurrencyCode(840);

        MockHttpSession session = login(randomAccount().getUser());
        mockMvc.perform(post("/rest/account/search")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.toJSON(criteria))
                .session(session))
                .andExpect(status().isForbidden());
    }

    /**
     * Test list transactions by admin
     *
     * @throws Exception
     */
    @Test
    public void testListTransactions() throws Exception {
        Account account = randomAccount();
        List<Transaction> result = accountDao.listTransactions(account.getId());

        MockHttpSession session = loginAdmin();
        String url = String.format("/rest/account/%d/tx", account.getId());
        mockMvc.perform(get(url).session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(result.size())));
    }

    /**
     * Test list transactions by account owner user
     *
     * @throws Exception
     */
    @Test
    public void testListTransactionsAccountOwner() throws Exception {
        Account account = randomAccount();
        List<Transaction> result = accountDao.listTransactions(account.getId());

        MockHttpSession session = login(account.getUser());
        String url = String.format("/rest/account/%d/tx", account.getId());
        mockMvc.perform(get(url).session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(result.size())));
    }

    /**
     * Test list transactions by account owner user
     *
     * @throws Exception
     */
    @Test
    public void testListTransactionsNoAdmin() throws Exception {
        Account account = randomAccount();
        Account caller = randomAccountNotUser(account.getId());

        MockHttpSession session = login(caller.getUser());
        String url = String.format("/rest/account/%d/tx", account.getId());
        mockMvc.perform(get(url).session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testTransferAmount() throws Exception {
        // fetch pre-config data
        Account accountS = randomAccount();
        Account accountT = randomAccountNot(accountS.getId());

        MockHttpSession session = login(accountS.getUser());

        TransferOrder order = new TransferOrder(accountT.getId(), BigDecimal.ONE, 840);
        String url = String.format("/rest/bank/%d/%d/transfer", accountS.getUser().getId(), accountS.getId());
        final List<Long> transactionIdHolder = new ArrayList<>();
        mockMvc.perform(post(url).contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.toJSON(order)).session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(mvcResult -> transactionIdHolder.add(Long.parseLong(mvcResult.getResponse().getContentAsString())));

        // wait for async method to complete
        Thread.sleep(5000);
        Transaction tx = em.find(Transaction.class, transactionIdHolder.get(0));
        Assert.assertTrue(tx.getTxAmount().compareTo(BigDecimal.ONE) == 0);
        Assert.assertTrue(tx.getState() == Transaction.STATE_CONFIRMED);
    }

    @Test
    public void testAdminListCurrencies() throws Exception {
        mockMvc.perform(get("/rest/bank/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
