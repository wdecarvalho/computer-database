import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan(basePackages = { "com.excilys" })
public class ServerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);

    @Autowired
    private Environment environment;

    /**
     * Initialise la DataSource et configure HikariCP.
     * @return DataSource
     * @throws IOException
     *             IOException
     */
    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws IOException {
        final Properties aProperties = new Properties();
        final InputStream path = ClassLoader.getSystemClassLoader().getResourceAsStream("app.properties");
        aProperties.load(path);
        try {
            Class.forName(aProperties.getProperty("dataSource.driverClassName"));
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Base de donnée utilisée : " + aProperties.getProperty("jdbcUrl"));
        HikariConfig hikariConfig = new HikariConfig(aProperties);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        return new HikariDataSource(hikariConfig);
    }

}
