package ma.gestcom.devdata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * DEV ONLY. Creates the PostgreSQL database (e.g. "gestcom") if it does not exist yet, before the DataSource and
 * Flyway start, so a fresh local PostgreSQL needs no manual SQL. Registered in META-INF/spring.factories.
 * Disabled outside the "dev" profile or with {@code gestcom.dev.create-database=false}.
 */
public class DevDatabaseCreator implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Pattern URL = Pattern.compile("^(jdbc:postgresql://[^/]+)/([A-Za-z0-9_]+)(\\?.*)?$");

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();
        boolean dev = Arrays.asList(env.getActiveProfiles()).contains("dev");
        if (!dev || !env.getProperty("gestcom.dev.create-database", Boolean.class, true)) {
            return;
        }
        String url = env.getProperty("spring.datasource.url");
        Matcher m = url == null ? null : URL.matcher(url);
        if (m == null || !m.matches()) {
            return;
        }
        String database = m.group(2);
        String user = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        try (Connection c = DriverManager.getConnection(m.group(1) + "/postgres", user, password)) {
            if (!exists(c, database)) {
                try (Statement s = c.createStatement()) {
                    s.executeUpdate("CREATE DATABASE \"" + database + "\" ENCODING 'UTF8'");
                }
                System.out.println("[gestcom] Base de données « " + database + " » créée");
            }
        } catch (SQLException e) {
            System.err.println("[gestcom] Impossible de vérifier/créer la base « " + database + " » : " + e.getMessage());
        }
    }

    private static boolean exists(Connection c, String database) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select 1 from pg_database where datname = ?")) {
            ps.setString(1, database);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
