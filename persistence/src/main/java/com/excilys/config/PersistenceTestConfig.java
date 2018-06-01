package com.excilys.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(basePackages = { "com.excilys.dao" })
@ComponentScan(basePackages = { "com.excilys.dao" })
public class PersistenceTestConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceTestConfig.class);

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
        LOGGER.info("Base de données utilisée : " + aProperties.getProperty("jdbcUrl"));
        final DataSource dSource = configureDataSource(aProperties);
        runScriptForDatabaseConnection(dSource);
        return dSource;
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
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        hibernateProperties.put("hibernate.show_sql", false);
        return hibernateProperties;
    }

}
