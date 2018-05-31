import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.excilys.dao" })
@ComponentScan(basePackages = { "com.excilys.dao", "com.excilys.service", "com.excilys.ui" })
public class CliConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliConfiguration.class);

    /**
     * Initialise la DataSource et configure HikariCP.
     * @return DataSource
     * @throws IOException
     *             IOException
     */
    @Bean
    public DataSource dataSource() {
        final Properties aProperties = new Properties();
        final InputStream path = ClassLoader.getSystemClassLoader().getResourceAsStream("app.properties");
        try {
            aProperties.load(path);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Base de donnée utilisée : " + aProperties.getProperty("jdbcUrl"));
        final DataSource dSource = configureDataSource(aProperties);
        return dSource;
    }

    /**
     * Creer un entityManagerFactory.
     * @return EntityManagerFactory
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setJpaVendorAdapter(getJpaVendorAdapter());
        lcemfb.setDataSource(dataSource());
        lcemfb.setPersistenceUnitName("JpaPersistenceUnit");
        lcemfb.setPackagesToScan("com.excilys.model");
        lcemfb.setJpaProperties(hibernateProperties());
        return lcemfb;
    }

    /**
     * Creer un HibernateJpaVendorAdapter.
     * @return JpaVendorAdapter
     */
    @Bean
    public JpaVendorAdapter getJpaVendorAdapter() {
        JpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        return adapter;
    }

    /**
     * Set hibernateProperties.
     * @return Properties setted
     */
    private Properties hibernateProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        hibernateProperties.put("hibernate.show_sql", true);
        return hibernateProperties;
    }

    /**
     * Creer un localeResolver a default en anglais.
     * @return SessionLocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.FRENCH);
        return cookieLocaleResolver;
    }

    /**
     * Creer un message source pour indiquer on sont situer les messages.
     * @return MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * Creer un JpaTransactionManager.
     * @return PlatformTransactionManager
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager txManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(
                getEntityManagerFactoryBean().getObject());
        return jpaTransactionManager;
    }

    /**
     * Creer la premiere connexion avec la bonne config.
     * @param aProperties
     *            Propriété pour la connexion
     * @return DataSource
     */
    private DataSource configureDataSource(final Properties aProperties) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(aProperties.getProperty("dataSource.driverClassName"));
        dataSource.setUrl(aProperties.getProperty("jdbcUrl"));
        dataSource.setUsername(aProperties.getProperty("dataSource.user"));
        dataSource.setPassword(aProperties.getProperty("dataSource.password"));
        return dataSource;
    }

}
