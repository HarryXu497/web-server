package database.statement.update;

/**
 * A helper class used to construct an SQL update statement.
 * Provides a fluent API with method chaining with the goal
 * of making SQL statements easier to construct.
 * @author Tommy Shan
 * @version 1.0 - June 2nd 2023
 */
public class UpdateSQLStatement {
    /** The table whose records to update */
    private String table;

    /** The condition under which to update the records*/
    private String condition;

    /** The columns to update */
    private String[] columns;

    /** The values to update the columns with*/
    private String[] values;

    /**
     * update
     * Specifies the table to update
     * @param table the table to update
     * @return this object to allow for method chaining
     * @throws NullPointerException if {@code table} is null
     */
    public UpdateSQLStatement update(String table) {
        if (table == null) {
            throw new NullPointerException("table cannot be null");
        }

        this.table = table;
        return this;
    }

    /**
     * columns
     * Specifies the columns to update
     * @param columns the columns to update
     * @return this object to allow for method chaining
     * @throws IllegalStateException if this method is called before specifying the table to update
     *                               using {@link #update(String)}
     * @throws NullPointerException if {@code columns} is null
     * @throws IllegalArgumentException if {@code columns} is an empty array
     */
    public UpdateSQLStatement columns(String... columns) {
        if (this.table == null) {
            throw new IllegalStateException("the table should be specified first");
        }

        if (columns == null) {
            throw new NullPointerException("columns cannot be null");
        }

        if (columns.length == 0) {
            throw new IllegalArgumentException("at least one column should be specified");
        }

        this.columns = columns;
        return this;
    }

    /**
     * values
     * Specifies the values to update the previously specified columns with
     * @param values the values to update with
     * @return this object to allow for method chaining
     * @throws IllegalStateException if this method is called before specifying the columns to update
     *                               using {@link #columns(String...)}}
     * @throws NullPointerException if {@code values} is null
     * @throws IllegalArgumentException if {@code values} does not have the same length as the
     *                                  previously specified columns
     */
    public UpdateSQLStatement values(String... values) {
        if (this.columns == null) {
            throw new IllegalStateException("the table should be specified first");
        }

        if (values == null) {
            throw new NullPointerException("columns cannot be null");
        }

        if (values.length != this.columns.length) {
            throw new IllegalArgumentException("The values and columns should have the same length");
        }

        this.values = values;
        return this;
    }

    /**
     * where
     * Specifies the condition to update the table.
     * @param condition the update condition
     * @return this object to allow for method chaining
     * @throws IllegalStateException if the values are not specified with {@link #values(String...)}
     *                               before invoking this method
     * @throws NullPointerException if {@code condition} is null
     */
    public UpdateSQLStatement where(String condition) {
        if (this.values == null) {
            throw new IllegalStateException("the values should be specified before invoking this method");
        }
        if (condition == null) {
            throw new NullPointerException("condition cannot be null");
        }

        this.condition = condition;
        return this;
    }

    /**
     * toString
     * Converts this object into a {@link String} SQL Statement
     * @return the SQL update statement as a string
     * @throws IllegalStateException if the condition is not specified with {@link #where(String)}
     *                               before calling this method
     */
    public String toString() {
        if (this.condition == null) {
            throw new IllegalArgumentException("The condition must be specified");
        }

        StringBuilder statement = new StringBuilder("UPDATE " + table + " SET ");

        // Build columns
        for(int i = 0; i < columns.length; i++) {
            statement.append(columns[i]).append(" = ").append(values[i]);
            if (i < columns.length - 1) {
                statement.append(", ");
            }
        }

        // Condition
        statement.append(" WHERE ");
        statement.append(this.condition);
        statement.append(";");

        return statement.toString();
    }
}
