package database.statement.insert;

/**
 * A helper class used to construct an SQL insert statement.
 * Provides a fluent API with method chaining with the goal
 * of making SQL statements easier to construct.
 * @author Tommy Shan
 * @version 1.0 - June 2nd 2023
 */
public class InsertSQLStatement {
    /** The columns to insert to */
    private String[] columns;

    /** The table to insert into */
    private String table;

    /** Whether to replace the value if it exists or not */
    private boolean replace;

    /** The values to insert*/
    private String[] values;

    /**
     * insertInto
     * Specifies the table to insert into
     * @param table the table to insert into
     * @return this object to allow for method chaining
     */
    public InsertSQLStatement insertInto(String table) {
        this.table = table;
        return this;
    }

    /**
     * orReplace
     * Specifies the statement to replace the entry if it already exists in the table.
     * @return this object to allow for method chaining
     * @throws IllegalStateException if the table is not specified with {@link #insertInto(String)}
     *                               before invoking this method
     */
    public InsertSQLStatement orReplace() {
        if (this.table == null) {
            throw new IllegalStateException("The table should be specified before calling this method");
        }

        this.replace = true;
        return this;
    }

    /**
     * columns
     * Specifies the columns to insert into.
     * @param columns the columns to insert values into
     * @return this object to allow for method chaining
     * @throws IllegalStateException if the table is not selected using {@link #insertInto(String)}
     *                               before calling this method.
     * @throws IllegalArgumentException if {@code columns} is an empty array
     * @throws NullPointerException if {@code columns} is null
     */
    public InsertSQLStatement columns(String... columns) {
        if (this.table == null) {
            throw new IllegalStateException("table must be selected before selecting columns");
        }
        if (columns == null) {
            throw new NullPointerException("columns should not be null");
        }
        if (columns.length == 0) {
            throw new IllegalArgumentException("At least 1 column should be selected.");
        }

        this.columns = columns;
        return this;
    }

    /**
     * values
     * Specifies the values to insert
     * @param values the values to insert into the table
     * @return this object to allow for method chaining
     * @throws IllegalStateException if the table or columns are not selected using {@link #insertInto(String)}
     *                               or {@link #columns(String...)}
     *                               before calling this method.
     * @throws IllegalArgumentException if the length of {@code values} does not match the length of
     *                                  the previously specified {@code columns}
     * @throws NullPointerException if {@code values} is null
     */
    public InsertSQLStatement values(String... values) {
        if (this.table == null) {
            throw new IllegalStateException("the table should be selected before inserting values");
        }
        if (this.columns == null) {
            throw new IllegalStateException("the columns should be selected before inserting values");
        }
        if (values == null) {
            throw new NullPointerException("values cannot be null");
        }
        if (columns.length != values.length) {
            throw new IllegalArgumentException("The amount of columns and values should be the same");
        }

        this.values = values;
        return this;
    }

    /**
     * toString
     * Converts this object into a {@link String} SQL Statement
     * @return the SQL insert statement as a string
     * @throws IllegalStateException if the values are not specified with {@link #values(String...)}
     *                               before calling this method
     */
    @Override
    public String toString() {
        // Check for state validity
        if (this.values == null) {
            throw new IllegalStateException("The statement insert values should not be null");
        }

        // Insert or replace
        String replace = "";

        if (this.replace) {
            replace = " OR REPLACE";
        }

        StringBuilder statement = new StringBuilder("INSERT" + replace + " INTO " + table + "(");

        // Build columns
        for(int i = 0; i < columns.length; i++) {
            statement.append(columns[i]);

            if (i < columns.length - 1) {
                statement.append(", ");
            }
        }

        // Build values
        statement.append(") VALUES (");

        for(int i = 0; i < values.length; i++) {
            statement.append(values[i]);
            if(i < values.length - 1) {
                statement.append(", ");
            }
        }

        statement.append(");");

        return statement.toString();
    }
}
