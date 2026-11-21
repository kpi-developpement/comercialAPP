package ma.gestcom;

import java.io.IOException;
import java.nio.file.Path;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.boot.SpringApplication;

/**
 * Runs the API with an embedded PostgreSQL (no Docker needed), data kept in {@code target/embedded-pg}.
 * <pre>./mvnw spring-boot:test-run -Dspring-boot.run.main-class=ma.gestcom.LocalDevApplication</pre>
 */
public final class LocalDevApplication {

    private static final int PORT = 54329;

    private LocalDevApplication() {
    }

    public static void main(String[] args) throws IOException {
        EmbeddedPostgres pg = EmbeddedPostgres.builder()
                .setPort(PORT)
                .setOverrideWorkingDirectory(Path.of("target", "embedded-pg-bin").toAbsolutePath().toFile())
                .setDataDirectory(Path.of("target", "embedded-pg"))
                .setCleanDataDirectory(false)
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                pg.close();
            } catch (IOException ignored) {
                // shutting down
            }
        }));
        System.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:" + PORT + "/postgres");
        System.setProperty("spring.datasource.username", "postgres");
        System.setProperty("spring.datasource.password", "postgres");
        SpringApplication.run(GestcomApplication.class, args);
    }
}
