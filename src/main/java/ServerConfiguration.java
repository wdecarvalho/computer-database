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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan(basePackages = { "com.excilys" })
public class ServerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);

    /**
     * Initialise la DataSource est configure HikariCP.
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
        if ("org.h2.Driver".equals(driver)) {
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
