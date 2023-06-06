package database.statement.delete;

public class DeleteSQLStatement {
    private String table;
    private String column;
    private String value;

    public DeleteSQLStatement deleteFrom(String table) {
        this.table = table;
        return this;
    }

    public DeleteSQLStatement where(String column) {
        if (this.table == null) {
            throw new IllegalStateException("table must be selected from before calling this method");
        }
        this.column = column;
        return this;
    }

    public DeleteSQLStatement equals(String value) {
        if (this.column == null) {
            throw new IllegalStateException("column must be indicated before deleting");
        }
        this.value = value;
        return this;
    }

    public String toString() {
        if((table == null) || (value == null) || (column == null)) {
            throw new IllegalStateException("Invalid table / value / column");
        }
        String ret = "DELETE FROM " + table + " WHERE " + column + " = " + value + ";";
        return ret;
    }
}
