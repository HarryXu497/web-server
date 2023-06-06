package database.statement.insert;

public class InsertSQLStatement {
    private String[] columns;
    private String table;
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

    public String toString() {
        if (value == null) {
            throw new IllegalStateException("The value should not be null.");
        }
        StringBuilder ret = new StringBuilder("INSERT INTO " + table + "(");
        for(int i = 0; i < columns.length; i++) {
            ret.append(columns[i]);
            if(i < columns.length - 1) {
                ret.append(", ");
            }
        }
        ret.append(") VALUES (");
        for(int i = 0; i < value.length; i++) {
            ret.append(value[i]);
            if(i < value.length - 1) {
                ret.append(", ");
            }
        }
        ret.append(");");
        return ret.toString();
    }
}
