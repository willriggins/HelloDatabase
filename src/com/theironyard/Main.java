package com.theironyard;

import org.h2.command.Prepared;
import org.h2.tools.Server;

import java.sql.*;

public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();

        Connection conn = DriverManager.getConnection("jdbc:h2:./main");

        Statement stmt = conn.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS players (id IDENTITY, name VARCHAR, is_alive BOOLEAN, score INT, health DOUBLE)");
        stmt.execute("INSERT INTO players VALUES (NULL, 'Alice', TRUE, 0 , 100)");
        stmt.execute("UPDATE players SET is_alive = FALSE WHERE name = 'Alice'");
        stmt.execute("DELETE FROM players WHERE name = 'Alice'");

        // BAD WAY! -- open to vulnerabilities

//        String name = "', TRUE, 0, 0); DROP TABLE players; --";
//        stmt.execute(String.format("INSERT INTO players VALUES (NULL, '%s', TRUE, 0, 100)", name));

        // GOOD WAY:
        String name = "Charlie";
        PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO players VALUES (NULL, ?, ?, 0, 100)");
        stmt2.setString(1, name);
        stmt2.setBoolean(2, true);
        stmt2.execute();

        PreparedStatement stmt3 = conn.prepareStatement("SELECT * FROM players");
        ResultSet results = stmt3.executeQuery();


        //not hasNext here.. they were not consistent.
        while (results.next()) {
            int id = results.getInt("id");
            String name2 = results.getString("name");
            boolean isAlive = results.getBoolean("is_alive");
            System.out.println(id + " " + name2 + " " + isAlive);
        }

    }
}
