package mysql_model;

import com.sun.istack.internal.Nullable;
import proj_exeptions.QueryException;

import java.util.Objects;

import static proj_contract.SQLContract.*;

/**
 * The {@code Query} class  represents a valid {@code JDBC} query
 * relaying on {@code QueryBuilder} object to validate a valid table columns values.
 * instance is immutable class,
 * can be used to create only one query.
 */
public class TableQuery {
    private final String query;

    /**
     * Initializes a newly created {@link TableQuery} object
     * using {@code QueryBuilder}
     *
     * @param tableQueryBuilder the builder this {@code TableQuery} object use to construct.
     */
    private TableQuery(TableQueryBuilder tableQueryBuilder) {
        this.query = tableQueryBuilder.builder.toString();
    }

    /**
     * get the parsed Query as a String.
     *
     * @return {@code String} value contained {@link TableQueryBuilder} parsed query.
     */
    public String getQuery() {
        return query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableQuery tableQuery1 = (TableQuery) o;
        return Objects.equals(query, tableQuery1.query);
    }

    @Override
    public int hashCode() {
        return query != null ? query.hashCode() : 0;
    }

    /**
     * The {@link TableQueryBuilder} class Helps to build
     * a valid {@link TableQuery} object to create a simple valid table content query.
     */
    public static class TableQueryBuilder {
        private StringBuilder builder;
        private String primaryKey;

        /**
         * @param primaryKey the primary key that represents this {@code TableQueryBuilder} object.
         *                   if {@param primaryKey} is null table will not have any Primary Key.
         */
        public TableQueryBuilder(@Nullable String primaryKey) {
            initBuilder();
            this.primaryKey = primaryKey;
        }

        /**
         * initialize the builder in addition
         * {@code StringBuilder.append()} used to append the first parsing entity.
         */
        private void initBuilder() {
            this.builder = new StringBuilder();
            this.builder.append(PARENTHESES_RIGHT).append(SPACE);
        }

        public TableQueryBuilder appendColumnQuery(String columnName, QueryType queryType, boolean notNull, boolean autoIncrement) throws QueryException {
            if ((columnName != null && !columnName.isEmpty()) && queryType != null) {
                if (queryType.equals(QueryType.NOT_NULL))
                    throw new QueryException("Use boolean param to set queries nullAbles.");
                this.builder.append(columnName).append(SPACE).append(queryType.getValue()).append(SPACE);
            }
            if (notNull)
                this.builder.append(QueryType.NOT_NULL.getValue()).append(SPACE);
            if (autoIncrement)
                this.builder.append(QueryType.AUTO_INCREMENT.getValue());
            this.builder.append(SPACE).append(SINGLE_HINT_MARK).append(SPACE);
            return this;
        }

        public void setPrimaryKey(String columnName) {
            if (columnName != null && !columnName.trim().isEmpty())
                this.primaryKey = columnName;
        }

        public TableQuery create() throws QueryException {
            if (this.builder.length() < MIN_TABLE_Q_SIZE)
                throw new QueryException("Builder have no queries.");
            if (primaryKey != null)
                this.builder.append(QueryType.PRIMARY_KEY.getValue()).append(SPACE).append(PARENTHESES_RIGHT).append(SPACE)
                        .append(this.primaryKey).append(SPACE).append(PARENTHESES_LEFT).append(SPACE).append(PARENTHESES_LEFT);
            else
                this.builder.delete(builder.lastIndexOf(String.valueOf(SINGLE_HINT_MARK)), builder.length()).append(PARENTHESES_LEFT);
            return new TableQuery(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TableQueryBuilder that = (TableQueryBuilder) o;
            if (!Objects.equals(builder, that.builder)) return false;
            return Objects.equals(primaryKey, that.primaryKey);
        }

        @Override
        public int hashCode() {
            int result = builder != null ? builder.hashCode() : 0;
            result = 31 * result + (primaryKey != null ? primaryKey.hashCode() : 0);
            return result;
        }
    }
}
