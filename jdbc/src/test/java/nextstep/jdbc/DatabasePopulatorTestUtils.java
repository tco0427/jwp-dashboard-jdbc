package nextstep.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class DatabasePopulatorTestUtils {

    private static final Logger log = LoggerFactory.getLogger(DatabasePopulatorTestUtils.class);

    public static void execute(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();) {
            final URL url = DatabasePopulatorTestUtils.class.getClassLoader().getResource("schema.sql");
            final File file = new File(Objects.requireNonNull(url).getFile());
            final String sql = Files.readString(file.toPath());
            statement.execute(sql);
        } catch (NullPointerException | IOException | SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private DatabasePopulatorTestUtils() {
    }
}