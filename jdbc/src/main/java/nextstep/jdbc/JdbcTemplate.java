package nextstep.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, @Nullable Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            setArgumentPreparedStatement(preparedStatement, args);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final RowMapperResultSetExtractor<T> rowMapperResultSetExtractor = new RowMapperResultSetExtractor<>(rowMapper);

            return rowMapperResultSetExtractor.extractData(resultSet);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, @Nullable Object... args) {
        final List<T> results = query(sql, rowMapper, args);
        try {
            return DataAccessUtils.nullableSingleResult(results);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int update(final String sql, @Nullable Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            setArgumentPreparedStatement(preparedStatement, args);

            return preparedStatement.executeUpdate();
        } catch(SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void setArgumentPreparedStatement(final PreparedStatement preparedStatement, final Object[] args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            final int parameterIndex = i + 1;
            preparedStatement.setObject(parameterIndex, args[i]);
        }
    }
}
