import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

import org.h2.tools.RunScript;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.excilys.dao" })
@ComponentScan(basePackages = { "com.excilys.dao", "com.excilys.service", "com.excilys.controleurs" })
public class ServerConfiguration implements WebMvcConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);

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
        String driver = null;
        try {
            aProperties.load(path);
            driver = aProperties.getProperty("dataSource.driverClassName");
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Base de donnée utilisée : " + aProperties.getProperty("jdbcUrl"));
        final DataSource dSource = configureDataSource(aProperties);
        if ("org.h2.Driver".equals(driver)) { // Selenium
            runScriptForDatabaseConnection(dSource);
        }
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
     * Creer un InternalResourceViewResolver pour trouver les fichiers jsp.
     * @return ViewResolver viewresolver
     */
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/jsp/");
        bean.setSuffix(".jsp");
        return bean;
    }

    /**
     * Creer un localeResolver a default en anglais.
     * @return SessionLocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }

    /**
     * Creer un localeChangeInterceptor qui change pour le param lang.
     * @return LocalChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
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

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/");
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

    /**
     * Lance le script pour preparer la base de données de test.
     * @param ds
     *            HikariDataSource
     */
    private void runScriptForDatabaseConnection(DataSource ds) {
        try (Connection connection = ds.getConnection()) {
            RunScript.execute(connection,
                    new FileReader(new File(ClassLoader.getSystemClassLoader().getResource("test_db.sql").toURI())));
        } catch (FileNotFoundException | SQLException | URISyntaxException e) {
            LOGGER.error(e.getMessage());
        }
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
}
