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
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan(basePackages = { "com.excilys.dao", "com.excilys.service", "com.excilys.ui" })
public class CliConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliConfiguration.class);

    /**
     * Initialise la DataSource et configure HikariCP.
     * @return DataSource
     * @throws IOException
     *             IOException
     */
    @Bean(destroyMethod = "close")
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
        try {
            Class.forName(aProperties.getProperty("dataSource.driverClassName"));
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Base de donnée utilisée : " + aProperties.getProperty("jdbcUrl"));
        final DataSource dSource = hikariConnectionInit(aProperties);
        if ("org.h2.Driver".equals(driver)) { // Selenium
            runScriptForDatabaseConnection(dSource);
        }
        return dSource;
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
     * Creer la premiere connexion avec la bonne config.
     * @param aProperties
     *            Propriété pour la connexion
     * @return DataSource
     */
    private DataSource hikariConnectionInit(final Properties aProperties) {
        final HikariConfig config = new HikariConfig(aProperties);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        return new HikariDataSource(config);
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
}
