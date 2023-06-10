package database.statement.query;

/**
 * A helper class used to construct an SQL query statement.
 * Provides a fluent API with method chaining with the goal
 * of making SQL statements easier to construct.
 * This class provides the additional functionality of a query on top of the base
 * functionality provided by {@link BaseQuerySQLStatement}.
 * @author Harry Xu
 * @version 1.0 - June 2nd 2023
 */
public class QuerySQLStatement {
    /** The selected columns of the query */
    private final String[] columns;

    /** The table to query */
    private final String table;

    /** The SQL condition for the query (i.e. the WHERE clause) */
    private String condition;

    /** The maximum size of the result set */
    private int limit;

    /** The direction of ordering */
    private Order order;

    /** The columns to order by */
    private String[] orderByColumns;

    /**
     * Constructs this query with the columns and table name
     * @param columns the selected columns
     * @param table the table to query
     * @throws NullPointerException if either of the columns or table arguments are null
     * @throws IllegalArgumentException if columns is an empty array
     */
    public QuerySQLStatement(String[] columns, String table) {
        if (columns == null) {
            throw new NullPointerException("columns cannot be null");
        }

        if (table == null) {
            throw new NullPointerException("table name cannot be null");
        }

        if (columns.length == 0) {
            throw new IllegalArgumentException("columns cannot have a length of 0");
        }

        this.columns = columns;
        this.table = table;

        // Default limit
        this.limit = -1;
    }

    /**
     * where
     * Specifies the WHERE condition of the SQL statement.
     * @param condition the condition
     * @return this object to allow for method chaining
     * @throws NullPointerException if the condition is null
     */
    public QuerySQLStatement where(String condition) {
        if (condition == null) {
            throw new NullPointerException("Condition cannot be null");
        }
        this.condition = condition;
        return this;
    }

    /**
     * limit
     * Specifies the limit of the result set.
     * @param limit the result set limit
     * @return this object to allow for method chaining
     * @throws IllegalArgumentException if the limit argument is negative
     */
    public QuerySQLStatement limit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("SQL LIMIT argument " + limit + " cannot be negative");
        }

        this.limit = limit;
        return this;
    }

    /**
     * orderBy
     * Specifies the order columns and the order direction
     * @param order the ordering direction
     * @param columns the columns to order by
     * @return this object to allow for method chaining
     * @throws NullPointerException if the order or columns are null
     * @throws IllegalArgumentException if {@code columns} is an empty array
     */
    public QuerySQLStatement orderBy(Order order, String... columns) {
        if (order == null) {
            throw new NullPointerException("The order direction cannot be null");
        }
        if (columns == null) {
            throw new NullPointerException("The columns cannot be null");
        }
        if (columns.length == 0) {
            throw new IllegalArgumentException("columns cannot be an empty array");
        }

        this.orderByColumns = columns;
        this.order = order;
        return this;
    }

    /**
     * toString
     * Converts this object into a {@link String} SQL Statement
     * @return the SQL query statement as a string
     */
    @Override
    public String toString() {
        StringBuilder statement = new StringBuilder();

        statement.append("SELECT ");

        // Columns
        statement.append(this.columns[0]);

        for (int i = 1; i < this.columns.length; i++) {
            statement.append(", ").append(this.columns[i]);
        }

        // Table
        statement.append(" FROM ");

        statement.append(this.table);

        // Where
        if (this.condition != null) {
            statement.append(" WHERE ");
            statement.append(this.condition);
        }

        // Limit
        if (this.limit != -1) {
            statement.append(" LIMIT ");
            statement.append(this.limit);
        }

        // Order
        if (this.orderByColumns != null) {
            statement.append(" ORDER BY ");

            statement.append(this.orderByColumns[0]);

            for (int i = 1; i < this.orderByColumns.length; i++) {
                statement.append(", ").append(this.orderByColumns[i]);
            }

            statement.append(" ").append(this.order.toString());
        }

        statement.append(";");

        return statement.toString();
    }
}