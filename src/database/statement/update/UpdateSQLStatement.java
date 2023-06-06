package database.statement.update;

public class UpdateSQLStatement {
    private String table;
    private String condition;
    private String[] columns;
    private String[] values;

    public UpdateSQLStatement update(String table) {
        this.table = table;
        return this;
    }

    public UpdateSQLStatement columns(String... columns) {
        if (this.table == null) {
            throw new IllegalStateException("table should be selected first");
        }

        if (columns.length == 0) {
            throw new IllegalArgumentException("at least one value should be specified");
        }

        this.columns = columns;
        return this;
    }

    public UpdateSQLStatement values(String... values) {
        if (this.columns == null) {
            throw new IllegalStateException("table should be selected first");
        }

        if (values.length == 0) {
            throw new IllegalArgumentException("at least one value should be specified");
        }

        this.values = values;
        return this;
    }

    public UpdateSQLStatement where(String condition) {
        if (this.values == null) {
            throw new IllegalStateException("values should be specified first");
        }
        this.condition = condition;
        return this;
    }

    public String toString() {
        if((columns.length != values.length) || (columns.length == 0)) {
            throw new IllegalArgumentException("HARRY BAD");
        }
        StringBuilder ret = new StringBuilder("UPDATE " + table + " SET ");
        for(int i = 0; i < columns.length; i++) {
            ret.append(columns[i]).append(" = ").append(values[i]);
            if(i < columns.length - 1) ret.append(", ");
        }
        ret.append(" WHERE ");
        ret.append(this.condition);
        ret.append(";");
        return ret.toString();
    }
}
