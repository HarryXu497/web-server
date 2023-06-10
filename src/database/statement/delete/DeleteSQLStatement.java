package database.statement.delete;

/**
 * A helper class used to construct an SQL delete statement.
 * Provides a fluent API with method chaining with the goal
 * of making SQL statements easier to construct.
 * @author Tommy Shan
 * @version 1.0 - June 2nd 2023
 */
public class DeleteSQLStatement {
    /** The table to delete from */
    private String table;

    /** The condition to delete from the table */
    private String condition;

    /**
     * deleteFrom
     * Specifies the table to delete records from.
     * @param table the table to delete from
     * @return this object to allow for method chaining
     * @throws NullPointerException if {@code table} is null
     */
    public DeleteSQLStatement deleteFrom(String table) {
        if (table == null) {
            throw new NullPointerException("table cannot be null");
        }
        this.table = table;
        return this;
    }

    /**
     * where
     * Specifies the condition under which to delete records
     * @param condition the deletion condition
     * @return this object to allow for method chaining
     * @throws NullPointerException if {@code condition} is null
     */
    public DeleteSQLStatement where(String condition) {
        if (this.table == null) {
            throw new IllegalStateException("table must be specified before calling this method");
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
     * @return the SQL delete statement as a string
     * @throws IllegalStateException if the values are not specified with {@link #where(String)}
     *                               before calling this method
     */
    @Override
    public String toString() {
        if (this.condition == null) {
            throw new IllegalStateException("A condition must be specified");
        }

        return "DELETE FROM " + table + " WHERE " + condition + ";";
    }
}
