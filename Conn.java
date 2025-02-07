package electricity.billing.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class Conn {
    Connection c;
    Statement s;

    public Conn() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Use allowPublicKeyRetrieval=true to fix authentication issue
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebs?useSSL=false&allowPublicKeyRetrieval=true", "root", "suman1234");
            s = c.createStatement();
            System.out.println("Database Connected Successfully!");

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Make sure you have added the MySQL Connector JAR.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed! Please check:");
            System.out.println("- Is MySQL running?");
            System.out.println("- Are the database credentials correct?");
            System.out.println("- Does the 'ebs' database exist?");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return c;
    }

    public static void main(String[] args) {
        new Conn(); // Run this to test the connection
    }
}
