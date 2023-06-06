package database.statement.query;

public class QuerySQLStatement {
    private String[] columns;
    private String table;
    private String condition;
    private int limit;
    private Order order;
    private String[] orderByColumns;

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
        this.limit = -1;
    }

    public QuerySQLStatement where(String condition) {
        this.condition = condition;
        return this;
    }

    public QuerySQLStatement limit(int limit) {
        if (limit < 0) {
            throw new IllegalStateException("SQL LIMIT statement cannot be negative");
        }

        this.limit = limit;
        return this;
    }

    public QuerySQLStatement orderBy(Order order, String... columns) {
        this.orderByColumns = columns;
        this.order = order;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stmt = new StringBuilder();

        stmt.append("SELECT ");

        stmt.append(this.columns[0]);

        for (int i = 1; i < this.columns.length; i++) {
            stmt.append(", ").append(this.columns[i]);
        }

        stmt.append(" FROM ");

        stmt.append(this.table);

        if (this.condition != null) {
            stmt.append(" WHERE ");
            stmt.append(this.condition);
        }

        if (this.limit != -1) {
            stmt.append(" LIMIT ");
            stmt.append(this.limit);
        }

        if (this.orderByColumns != null) {
            stmt.append(" ORDER BY ");

            stmt.append(this.orderByColumns[0]);

            for (int i = 1; i < this.orderByColumns.length; i++) {
                stmt.append(", ").append(this.orderByColumns[i]);
            }

            stmt.append(" ").append(this.order.toString());
        }

        stmt.append(";");

        return stmt.toString();
    }
}