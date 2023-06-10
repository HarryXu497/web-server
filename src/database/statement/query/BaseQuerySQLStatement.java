package database.statement.query;

/**
 * A helper class used to construct an SQL query statement.
 * Provides a fluent API with method chaining with the goal
 * of making SQL statements easier to construct.
 * This class provides the basic functionality of a query: the selected columns and the table to query.
 * It then relies on the {@link QuerySQLStatement} to provide other functionality such as ordering and conditionals
 * @author Harry Xu
 * @version 1.0 - June 2nd 2023
 */
public class BaseQuerySQLStatement {
    /** The columns selected by the statement */
    private String[] columns;

    /**
     * select
     * Selects an arbitrary amount of columns for the query.
     * @param columns the columns to select
     * @return this object to allow for method chaining
     * @throws NullPointerException if {@code columns} are null
     * @throws IllegalArgumentException if {@code columns} is an empty array
     */
    public BaseQuerySQLStatement select(String... columns) {
        if (columns == null) {
            throw new NullPointerException("columns cannot be null");
        }
        if (columns.length == 0) {
            throw new IllegalArgumentException("columns cannot be an empty array");
        }

        this.columns = columns;
        return this;
    }

    /**
     * from
     * Specifies the table to query.
     * @param table the table to query
     * @return a {@link QuerySQLStatement} to chain more methods one
     * @throws IllegalStateException if the methods are not chained in the correct order
     * @throws NullPointerException if {@code table} is null
     */
    public QuerySQLStatement from(String table) {
        if (this.columns == null) {
            throw new IllegalStateException("SELECT statement must be made before specifying the table");
        }
        if (table == null) {
            throw new NullPointerException("table cannot be null");
        }

        return new QuerySQLStatement(this.columns, table);
    }
}
