package database.statement.query;

public class BaseQuerySQLStatement {
    private String[] columns;

    public BaseQuerySQLStatement select(String... columns) {
        this.columns = columns;
        return this;
    }

    public QuerySQLStatement from(String table) {
        if (this.columns == null) {
            throw new IllegalStateException("SELECT statement must be made before specifying the table");
        }

        return new QuerySQLStatement(this.columns, table);
    }
}
