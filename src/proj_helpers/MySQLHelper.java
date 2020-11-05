package proj_helpers;

import javafx.util.Pair;
import mysql_model.JDBCConnection;
import mysql_model.TableQuery;
import proj_exeptions.QueryException;

import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static proj_contract.SQLContract.*;

public class MySQLHelper {

    public static Connection getMySqlDBConnection() throws SQLException {
        return JDBCConnection.getInstance().getConnection();
    }

    /**
     * Creates Table if not exists in the {@code JDBC}.
     *
     * @param dbName     the database target name.
     * @param tableName  the table name.
     * @param tableQuery the {@link TableQuery} containing table content.
     * @param connection the {@link Connection} of the {@code JDBC}.
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     * or (2) 0 for SQL statements that return nothing.
     * @throws SQLException
     */
    public static int createTableNotExists(String dbName, String tableName, TableQuery tableQuery, Connection connection) throws SQLException {
        connection.setCatalog(dbName);
        return connection.createStatement().executeUpdate(CREATE_TABLE_NOT_EXISTS + tableName + tableQuery.getQuery());
    }

    /**
     * Creating a new Database in the {@code JDBC}.
     *
     * @param dbName the desired Database name.
     * @param con    the {@link Connection} to be used
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     * or (2) 0 for SQL statements that return nothing.
     * @throws SQLException
     */
    public static int createDataBaseNotExists(String dbName, Connection con) throws SQLException, QueryException {
        if (con.isClosed())
            throw new QueryException("Connection closed. ");
        return con.createStatement().executeUpdate(CREATE_DB_NOT_EXISTS + dbName);
    }

    public static void insertIntoSingle(Connection connection, String dbName, String tableName, QueryType type, Pair<String, Object> data) throws SQLException {
        connection.setCatalog(dbName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(INSERT_INTO).append(tableName).append(SPACE)
                .append(PARENTHESES_RIGHT).append(SPACE)
                .append(data.getKey())
                .append(SPACE).append(PARENTHESES_LEFT).append(SPACE)
                .append(VALUES).append(PARENTHESES_RIGHT).append(SPACE);
        switch (type) {
            case TEXT:
                stringBuilder.append(SINGLE_QUOTE_MARK).append(data.getValue()).append(SINGLE_QUOTE_MARK);
                break;
            case INTEGER:
                stringBuilder.append(data.getValue());
                break;
            case BOOLEAN:

                break;
        }
        stringBuilder.append(SPACE).append(PARENTHESES_LEFT).append(SPACE);
        connection.createStatement().execute(stringBuilder.toString());
    }

    public static int insertIntoSpecific(Connection connection, String dbName, String tableName, Map<String, Pair<QueryType, Object>> data) throws SQLException {
        connection.setCatalog(dbName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(INSERT_INTO).append(tableName)
                .append(SPACE).append(PARENTHESES_RIGHT).append(SPACE);
        Iterator<String> columnNames = data.keySet().iterator();
        while (columnNames.hasNext()) {
            stringBuilder.append(columnNames.next());
            if (!columnNames.hasNext()) {
                stringBuilder.append(SPACE).append(PARENTHESES_LEFT).append(SPACE);
                break;
            }
            stringBuilder.append(SINGLE_QUOTE_MARK);
        }
        stringBuilder.append(VALUES)
                .append(SPACE).append(PARENTHESES_RIGHT).append(SPACE);
        columnNames = data.keySet().iterator();
        Statement statement = connection.createStatement();
        while (columnNames.hasNext()) {
            Pair<QueryType, Object> singlePair = data.get(columnNames.next());
            switch (singlePair.getKey()) {
                case TEXT:
                    stringBuilder.append(SINGLE_QUOTE_MARK).append(singlePair.getValue()).append(SINGLE_QUOTE_MARK);
                    break;
                case INTEGER:
                    stringBuilder.append(singlePair.getValue());
                    break;
                case BOOLEAN:
                    stringBuilder.append((Boolean) singlePair.getValue() ? 1 : 0);
                    break;
            }

            if (!columnNames.hasNext()) {
                stringBuilder.append(SPACE).append(PARENTHESES_LEFT).append(SPACE);
                break;
            }
            stringBuilder.append(SINGLE_QUOTE_MARK);
        }
        return statement.executeUpdate(stringBuilder.toString());
    }


    /**
     * {@syntax INSERT INTO (?,?,..) VALUES (?,?,..)) helper method to insert a FULL ENTITY only
     * if the {@param pairList} values not symmetric to the {@code JDBC} table Incorrect values Exception thrown.
     * this method is relaying on the {@link #extractTableColumns(Connection, String)} method.
     *
     * @param connection the {@code Connection} to initialize the request.
     * @param dbName     the {@code JDBC} data base name.
     * @param tableName  the table name of the desired table.
     * @param pairList   list of {@code Pair<QueryType,?>} representing entity
     *                   using wildCard unbounded type to enhance versatile approach.
     * @return {@code true} if the request was successfully processed.
     * @throws SQLException
     * @throws QueryException
     */
    public static int insertEntityInto(Connection connection, String dbName, String tableName, List<Pair<QueryType, ?>> pairList) throws SQLException, QueryException {
        if (connection.isClosed())
            throw new QueryException("Cannot operate closed connection.");
        connection.setCatalog(dbName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(INSERT_INTO).append(tableName)
                .append(extractTableColumns(connection, tableName))
                .append(VALUES)
                .append(PARENTHESES_RIGHT).append(SPACE);
        for (int i = 0; i < pairList.size() - 1; i++)
            stringBuilder.append(QUESTION_MARK).append(SPACE).append(SINGLE_HINT_MARK).append(SPACE)
                    .append(SPACE).append(QUESTION_MARK).append(SPACE).append(PARENTHESES_LEFT);
        PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString());
        int counter = 0;
        for (Pair<QueryType, ?> queryTypePair : pairList) {
            switch (queryTypePair.getKey()) {
                case TEXT:
                    preparedStatement.setString(++counter, (String) queryTypePair.getValue());
                    break;
                case INTEGER:
                    preparedStatement.setInt(++counter, ((Number) queryTypePair.getValue()).intValue());
                    break;
                case BOOLEAN:
                    preparedStatement.setBoolean(++counter, (Boolean) queryTypePair.getValue());
                    break;
                //Do some more work..(cases)
            }
        }
        return preparedStatement.executeUpdate();
    }

    /**
     * private method helping construct the sql statement including validation
     *
     * @param connection the {@code Connection} executing the prepared statement
     * @param tableName  the table name of the desired table.
     * @return {@code String} valid sql statement
     * *Note* AUTO_INCREMENT values will be discard.
     * @throws SQLException if the table couldn't be accessed.
     * @see #insertEntityInto(Connection, String, String, List)
     */
    private static String extractTableColumns(Connection connection, String tableName) throws SQLException {
        StringBuilder builder = new StringBuilder();
        ResultSet set;
        Statement statement = connection.createStatement();
        set = statement.executeQuery(SELECT_ALL_FROM + tableName + SPACE);
        ResultSetMetaData metaData = set.getMetaData();
        int columnCounter = metaData.getColumnCount();
        builder.append(SPACE).append(PARENTHESES_RIGHT).append(SPACE);
        for (int i = 1; i < columnCounter; i++) {
            if (!metaData.isAutoIncrement(i))
                builder.append(metaData.getColumnLabel(i))
                        .append(SPACE).append(SINGLE_HINT_MARK).append(SPACE);
        }
        builder.append(metaData.getColumnLabel(columnCounter))
                .append(SPACE).append(PARENTHESES_LEFT).append(SPACE);
        return builder.toString();
    }

    /**
     * get the {@code JDBC} tablesMeta data.
     *
     * @param connection the {@code Connection} executing the prepared statement
     * @param tableName  the table name of the desired table.
     * @return {@code String} valid sql statement
     * @throws SQLException if the table couldn't be accessed.
     */
    private static String getTableColumnsData(Connection connection, String tableName) throws SQLException {
        StringBuilder builder = new StringBuilder();
        ResultSet set;
        Statement statement = connection.createStatement();
        set = statement.executeQuery(SELECT_ALL_FROM + tableName + SPACE);
        ResultSetMetaData metaData = set.getMetaData();
        int columnCounter = metaData.getColumnCount();
        builder.append(SPACE).append(PARENTHESES_RIGHT).append(SPACE);
        for (int i = 1; i < columnCounter; i++) {
            builder.append(metaData.getColumnLabel(i))
                    .append(SPACE).append(SINGLE_HINT_MARK).append(SPACE);

        }
        builder.append(metaData.getColumnLabel(columnCounter))
                .append(SPACE).append(PARENTHESES_LEFT).append(SPACE);
        return builder.toString();
    }
}
