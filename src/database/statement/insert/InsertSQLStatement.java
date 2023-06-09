package database.statement.insert;

public class InsertSQLStatement {
    private String[] columns;
    private String table;
    private boolean replace;
    private String[] value;

    public InsertSQLStatement columns(String... columns) {
        if (this.table == null) {
            throw new IllegalStateException("table must be selected before selecting columns");
        }
        if(columns.length == 0) {
            throw new IllegalArgumentException("At least 1 column should be selected.");
        }
        this.columns = columns;
        return this;
    }

    public InsertSQLStatement insertInto(String table) {
        this.table = table;
        return this;
    }

    public InsertSQLStatement orReplace() {
        this.replace = true;
        return this;
    }

    public InsertSQLStatement values(String... value) {
        if (this.table == null) {
            throw new IllegalStateException("table should be selected before inserting values");
        }
        if (this.columns == null) {
            throw new IllegalStateException("columns should be selected before inserting values");
        }
        if (columns.length != value.length) {
            throw new IllegalArgumentException("The amount of columns and the amount of values must match");
        }

        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        if (this.value == null) {
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

        // Biild values
        statement.append(") VALUES (");

        for(int i = 0; i < value.length; i++) {
            statement.append(value[i]);
            if(i < value.length - 1) {
                statement.append(", ");
            }
        }

        statement.append(");");

        return statement.toString();
    }
}
