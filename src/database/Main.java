package database;

import database.dao.ProblemDatabase;
import database.dao.UserDatabase;
import database.model.Problem;
import database.model.Role;
import database.model.User;
import database.statement.SQLStatement;
import database.statement.delete.DeleteSQLStatement;
import database.statement.insert.InsertSQLStatement;
import database.statement.query.Order;

import database.statement.update.UpdateSQLStatement;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Problem problem1 = new Problem(0, 1, "Trianglane",
                "Tommy feels sleepy during geography class, so he decides to do his math homework. He is shocked when he opened his homework packaged that's assigned my Mr. Choi...\n" +
                        "\n" +
                        "It's too hard for him, a grade 9 student, to solve the problem. So he asked you to help him solve the problem for him.\n" +
                        "\n" +
                        "Given two numbers A and B, determine the value of A + B.", "Simple Math", LocalDateTime.now(), null,1);

        User user1 = new User("Tommy_Shan", 1, 1000, 1000, 1000000, Role.ADMIN, "12345");
        User user2 = new User("Harry_Xu", 2, 1000, 1000, 1000000, Role.ADMIN, "54321");
        User user3 = new User("Harry_Bad", 3, 0, 0, 100000000, Role.USER, "BAD");

        UserDatabase userDatabase = new UserDatabase();
        userDatabase.addUser(user1);
        userDatabase.addUser(user2);
        userDatabase.addUser(user3);
        User resuser = userDatabase.getUserById(1);
        System.out.println("Get database.model.User with ID = " + resuser.getUserID() + " - " + resuser.getUserName());

        ProblemDatabase problemDatabase = new ProblemDatabase();
        problemDatabase.addProblem(problem1);
        Problem res = problemDatabase.getProblemById(1);
        System.out.println("Get database.model.Problem with ID = " + res.getProblemID() + " - " + res.getTitle());

        String inscmd = new InsertSQLStatement().insertInto("USERLIST").columns("NAME", "AGE").values(new String[] {"HARRYBAD", "121"}).toString();
        System.out.println(inscmd);

        System.out.println(
                SQLStatement.selectStatement()
                        .select("name", "age")
                        .from("users")
                        .where("age > 16")
                        .limit(10)
                        .orderBy(Order.DESC, "age")
        );

        System.out.println(
                new DeleteSQLStatement()
                        .deleteFrom("users")
                        .where("name")
                        .equals("\"Tommy\"")
        );

        System.out.println(
                new InsertSQLStatement()
                        .insertInto("users")
                        .columns("name", "age")
                        .values("\"Tommy\"", "13")
                        .toString()
        );

        System.out.println(
                new UpdateSQLStatement()
                        .update("users")
                        .columns("name", "age")
                        .values("\"Harry\"", "15")
                        .where("name = \"Tommy\"")
        );
        byte[] salt = null;
        try {
            salt = UserDatabase.getSalt();
        } catch (NoSuchAlgorithmException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println(UserDatabase.hashPassword("TommyLovesCP", salt));
        System.out.println(UserDatabase.hashPassword("TommyLovesCP", salt));
        System.out.println(UserDatabase.hashPassword("tommyLovesCP", salt));
    }
}