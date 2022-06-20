/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roster;

import domain.Event;
import domain.EventInvitation;
import domain.Role;
import domain.Status;
import domain.User;
import service.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Liam
 */
public class Database {

    public static Connection dbConnection = null;
    private static String dbURL = "jdbc:sqlite:SQLiteDB.db";

    /**
     * Reads sql files and creates the database schema
     *
     * @param dbConnection
     */
    public static Connection initDatabase() {
        try {
            if (!new File("SQLiteDB.db").exists()) {
                //String sql = _sqlFileToString("../database/script/create_tables.sql");
                String sql = "CREATE TABLE IF NOT EXISTS  Role (\n" +
                        "\troleID INTEGER NOT NULL PRIMARY KEY,\n" +
                        "\troleName varchar(50) NOT NULL\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE IF NOT EXISTS  Status (\n" +
                        "\tstatusID INTEGER NOT NULL PRIMARY KEY,\n" +
                        "\tstatusName varchar(50) NOT NULL\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE IF NOT EXISTS  User (\n" +
                        "\tuserID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                        "\tfirstName varchar(50) NOT NULL,\n" +
                        "\tlastName varchar(50) NOT NULL,\n" +
                        "\temail varchar(100) NOT NULL,\n" +
                        "\tpassword varchar(50) NULL,\n" +
                        "\tphoneNumber varchar(15) NULL,\n" +
                        "\troleID Integer NOT NULL, \t\n" +
                        "\n" +
                        "\tFOREIGN KEY (roleID )\n" +
                        "       REFERENCES Role (roleID )\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE IF NOT EXISTS  EventInvitation (\n" +
                        "\tuserID INTEGER NOT NULL, \n" +
                        "\teventID INTEGER NOT NULL,\n" +
                        "\tisAccepted bit,\n" +
                        "\t\n" +
                        "\tFOREIGN KEY (userID)\n" +
                        "       REFERENCES User (userID),\n" +
                        "\t   \n" +
                        "\tFOREIGN KEY (eventID)\n" +
                        "       REFERENCES Event (eventID)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE IF NOT EXISTS  Event (\n" +
                        "\teventID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                        "\tuserID INTEGER NOT NULL,\n" +
                        "\tdate datetime NOT NULL,\n" +
                        "\tlocation varchar(100),\n" +
                        "\tduration REAL NOT NULL,\n" +
                        "\tdescription text NOT NULL,\n" +
                        "\tstatusID INTEGER NOT NULL,\n" +
                        "\t\n" +
                        "\tFOREIGN KEY (userID)\n" +
                        "       REFERENCES User (userID),\n" +
                        "\t\n" +
                        "\tFOREIGN KEY (statusID)\n" +
                        "       REFERENCES Status (statusID)\n" +
                        ");";

                mutate(sql);
            }
            return DriverManager.getConnection(dbURL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void insertDefaultValues() {
        if (App.dao != null) {
            // Add default roles
            App.dao.insertRole(new Role(1, "Admin"));
            App.dao.insertRole(new Role(2, "Staff"));

            User adminUser = new User(1, "Admin", "User", "admin@test.com", User.getPasswordHash("admin"), "1234567890", 1);
            if (!adminUser.exists()) {
                App.dao.insertUser(adminUser);
            }

            User testUser1 = new User(2, "Test", "One", "test1@test.com", User.getPasswordHash("test"), "1234567890", 2);
            if (!testUser1.exists()) {
                App.dao.insertUser(testUser1);
            }

            User testUser2 = new User(3, "Test", "Two", "test2@test.com", User.getPasswordHash("test"), "1234567890", 2);
            if (!testUser2.exists()) {
                App.dao.insertUser(testUser2);
            }

            // Add default statuses
            App.dao.insertStatus(new Status(1, "Planned"));
            App.dao.insertStatus(new Status(2, "Complete"));
            App.dao.insertStatus(new Status(3, "Cancelled"));
            App.dao.insertStatus(new Status(4, "Postponed"));

            Event newEvent = new Event(1, 1, "2022-04-28 12:00", "Dunedin", 1.0, "This is a test event", 1);
            if (!newEvent.exists()) {
                App.dao.insertEvent(newEvent);
            }

            Event newEvent2 = new Event(2, 1, "2022-04-28 15:00", "Dunedin", 3.0, "This is another test event", 1);
            if (!newEvent2.exists()) {
                App.dao.insertEvent(newEvent2);
            }

            Event newEvent3 = new Event(3, 1, "2022-04-27 12:00", "Dunedin", 1.0, "This is a completed test event", 2);
            if (!newEvent3.exists()) {
                App.dao.insertEvent(newEvent3);
            }

            EventInvitation newInvite = new EventInvitation(2, 1, true);
            if (!newInvite.exists()) {
                App.dao.insertEventInvitation(newInvite);
            }

            EventInvitation newInvite2 = new EventInvitation(2, 2, true);
            if (!newInvite2.exists()) {
                App.dao.insertEventInvitation(newInvite2);
            }

            EventInvitation newInvite3 = new EventInvitation(2, 3, true);
            if (!newInvite3.exists()) {
                App.dao.insertEventInvitation(newInvite3);
            }

            EventInvitation newInvite4 = new EventInvitation(3, 1, false);
            if (!newInvite4.exists()) {
                App.dao.insertEventInvitation(newInvite4);
            }

        }
    }

    /**
     * Opens db connection and runs command
     *
     * @return whether script was successful
     */
    public static boolean mutate(String command) {
        if (command != null) {
            try {
                dbConnection = DriverManager.getConnection(dbURL);
                Statement stmt = dbConnection.createStatement();
                stmt.executeUpdate(command);
                stmt.close();
                dbConnection.close();
                return true;
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    System.out.println(ex.getMessage());
                    return false;
                }
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Reads sql file and converts into executable string
     *
     * @param path to sql file
     * @return string
     */
    private static String _sqlFileToString(String path) {
        try {
            // Read sql file and convert to string
            FileReader fr = new FileReader(path);
            StringBuilder script = new StringBuilder();
            BufferedReader lineReader = new BufferedReader(fr);
            String line;
            while ((line = lineReader.readLine()) != null) {
                script.append(line);
                script.append(System.getProperty("line.separator", "\n"));
            }
            return script.toString();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
