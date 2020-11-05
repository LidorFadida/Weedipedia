package mysql_model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import static proj_contract.SQLContract.*;

public class JDBCConnection {
    private static JDBCConnection instance;
    private final Connection connection;


    private JDBCConnection() throws SQLException {
        Properties properties = new Properties();
        properties.put("user", DB_USER_NAME);
        properties.put("password", DB_PASSWORD);
        properties.put("serverTimeZone", "UTC");
        properties.put("useTimeZone", true);
        this.connection = DriverManager.getConnection(DB_URL, properties);
    }

    public static JDBCConnection getInstance() throws SQLException {
        if (instance == null)
            instance = new JDBCConnection();
        else if (instance.getConnection().isClosed())
            instance = new JDBCConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JDBCConnection that = (JDBCConnection) o;
        return Objects.equals(connection, that.connection);
    }

    @Override
    public int hashCode() {
        int result = connection != null ? connection.hashCode() : 0;
        result = 31 * result + DB_URL.hashCode();
        result = 31 * result + DB_USER_NAME.hashCode();
        result = 31 * result + DB_PASSWORD.hashCode();
        return result;
    }
}