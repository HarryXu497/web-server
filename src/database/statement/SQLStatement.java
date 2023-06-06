package database.statement;

import database.statement.delete.DeleteSQLStatement;
import database.statement.insert.InsertSQLStatement;
import database.statement.query.BaseQuerySQLStatement;
import database.statement.update.UpdateSQLStatement;

public class SQLStatement {
    public static UpdateSQLStatement updateStatement() {
        return new UpdateSQLStatement();
    }

    public static BaseQuerySQLStatement selectStatement() {
        return new BaseQuerySQLStatement();
    }

    public static DeleteSQLStatement deleteStatement() {
        return new DeleteSQLStatement();
    }

    public static InsertSQLStatement insertStatement() {
        return new InsertSQLStatement();
    }
}
