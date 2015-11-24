package ru.ulfr.poc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.ulfr.poc.modules.bank.BankDao;
import ru.ulfr.poc.modules.bank.BankDaoImpl;
import ru.ulfr.poc.modules.users.UserDao;
import ru.ulfr.poc.modules.users.UserDaoImpl;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Spring configuration class for Database access
 * <p/>
 * Initializes Database Connection Pool, Hibernate, Entity Manager factory, Transaction manager
 * <p/>
 * Instantiates Data Access Object Beans
 */
@Configuration
@EnableJpaRepositories(basePackages = {
        "ru.ulfr.poc"
})
@EnableTransactionManagement
public class PersistenceContext {

    @Bean(destroyMethod = "close")
    DataSource dataSource() {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(Config.DS_DRIVER);
        dataSourceConfig.setJdbcUrl(Config.DS_URL);
        dataSourceConfig.setUsername(Config.DS_USER);
        dataSourceConfig.setPassword(Config.DS_PASS);

        return new HikariDataSource(dataSourceConfig);
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("ru.ulfr.poc");

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
        jpaProperties.put("hibernate.show_sql", "false");
        jpaProperties.put("hibernate.format_sql", "true");

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean(name = "userDao")
    UserDao getUserDao() {
        return new UserDaoImpl();
    }

    @Bean(name = "bankDao")
    BankDao getBankDao() {
        return new BankDaoImpl();
    }
}
