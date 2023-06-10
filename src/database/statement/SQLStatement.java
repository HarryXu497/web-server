package database.statement;

import database.statement.delete.DeleteSQLStatement;
import database.statement.insert.InsertSQLStatement;
import database.statement.query.BaseQuerySQLStatement;
import database.statement.update.UpdateSQLStatement;

/**
 * Wrapper class which wraps all SQL builder classes.
 * Allows access to the 4 CRUD SQL statement builders
 * @author Tommy Shan
 * @version 1.0 - June 2nd 2023
 */
public class SQLStatement {
    /**
     * updateStatement
     * Creates and returns a {@link UpdateSQLStatement}
     * @return a {@link UpdateSQLStatement}
     */
    public static UpdateSQLStatement updateStatement() {
        return new UpdateSQLStatement();
    }

    /**
     * selectStatement
     * Creates and returns a {@link BaseQuerySQLStatement}
     * @return a {@link BaseQuerySQLStatement}
     */
    public static BaseQuerySQLStatement selectStatement() {
        return new BaseQuerySQLStatement();
    }

    /**
     * deleteStatement
     * Creates and returns a {@link DeleteSQLStatement}
     * @return a {@link DeleteSQLStatement}
     */
    public static DeleteSQLStatement deleteStatement() {
        return new DeleteSQLStatement();
    }

    /**
     * insertStatement
     * Creates and returns a {@link InsertSQLStatement}
     * @return a {@link InsertSQLStatement}
     */
    public static InsertSQLStatement insertStatement() {
        return new InsertSQLStatement();
    }
}
