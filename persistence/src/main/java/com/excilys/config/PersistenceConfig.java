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
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@EnableJpaRepositories(basePackages = { "com.excilys.dao" })
public class PersistenceConfig {

    private static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String ORG_HIBERNATE_DIALECT_H2_DIALECT = "org.hibernate.dialect.H2Dialect";
    private static final String ORG_HIBERNATE_DIALECT_MY_SQL5_DIALECT = "org.hibernate.dialect.MySQL5Dialect";
    private static final String HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String COM_EXCILYS_MODEL = "com.excilys.model";
    private static final String JPA_PERSISTENCE_UNIT = "JpaPersistenceUnit";
    private static final String TEST_DB_SQL = "test_db.sql";
    private static final String DATA_SOURCE_PASSWORD = "dataSource.password";
    private static final String DATA_SOURCE_USER = "dataSource.user";
    private static final String JDBC_URL = "jdbcUrl";
    private static final String DATA_SOURCE_DRIVER_CLASS_NAME = "dataSource.driverClassName";
    private static final String APP_PROPERTIES = "app.properties";
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceConfig.class);

    private static boolean test = false;
    /**
     * Initialise la DataSource et configure HikariCP.
     * @return DataSource
     * @throws IOException
     *             IOException
     */
    @Bean(name = "DataSource")
    public DataSource dataSource() {
        final Properties aProperties = new Properties();
        final InputStream path = ClassLoader.getSystemClassLoader().getResourceAsStream(APP_PROPERTIES);
        String driver = null;
        try {
            aProperties.load(path);
            driver = aProperties.getProperty(DATA_SOURCE_DRIVER_CLASS_NAME);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Base de données utilisée : " + aProperties.getProperty(JDBC_URL));
        final DataSource dSource = configureDataSource(aProperties);
        if (H2_DRIVER.equals(driver)) {
            test = true;
            runScriptForDatabaseConnection(dSource);
        }
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
        dataSource.setDriverClassName(aProperties.getProperty(DATA_SOURCE_DRIVER_CLASS_NAME));
        dataSource.setUrl(aProperties.getProperty(JDBC_URL));
        dataSource.setUsername(aProperties.getProperty(DATA_SOURCE_USER));
        dataSource.setPassword(aProperties.getProperty(DATA_SOURCE_PASSWORD));
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
                    new FileReader(new File(ClassLoader.getSystemClassLoader().getResource(TEST_DB_SQL).toURI())));
        } catch (FileNotFoundException | SQLException | URISyntaxException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Creer un entityManagerFactory.
     * @return EntityManagerFactory
     */
    @Bean(name = "entityManagerFactory")
    @DependsOn("DataSource")
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setJpaVendorAdapter(getJpaVendorAdapter());
        lcemfb.setDataSource(dataSource());
        lcemfb.setPersistenceUnitName(JPA_PERSISTENCE_UNIT);
        lcemfb.setPackagesToScan(COM_EXCILYS_MODEL);
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
        hibernateProperties.setProperty(HIBERNATE_HBM2DDL_AUTO, "update");
        String dialect = ORG_HIBERNATE_DIALECT_MY_SQL5_DIALECT;
        if(test) {
            dialect = ORG_HIBERNATE_DIALECT_H2_DIALECT;
        }
        hibernateProperties.put(HIBERNATE_DIALECT, dialect);
        hibernateProperties.put(HIBERNATE_SHOW_SQL, true);
        return hibernateProperties;
    }

}
