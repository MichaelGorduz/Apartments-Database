package org.example;

import java.sql.*;
import java.util.Scanner;

public class App {
    // CREATE DATABASE apartments;
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/apartments?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "samplepassword";
    static Connection conn;


    public static void main( String[] args ) {
        try {
            // Step 1: Establish the connection
            conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            if (conn != null) {
                System.out.println("Connected to the database.");
            }

            // Step 2: Create the table 'apts' if not exists
            createTable();

            // Step 4: Show the user interface to choose between options
            showUserInterface();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Step 5: Close the connection
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Connection closed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to create the table 'apts' if it doesn't exist
    private static void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS apts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "area DOUBLE NOT NULL," +
                "address VARCHAR(255) NOT NULL," +
                "square_ft DOUBLE NOT NULL," +
                "rooms_count INT NOT NULL," +
                "price DOUBLE NOT NULL" +
                ")";
        Statement statement = conn.createStatement();
        statement.executeUpdate(createTableSQL);
    }

    // Method to show the user interface and handle user input
    private static void showUserInterface() {
        try {
            while (true) {
                System.out.println("\nChoose an option:");
                System.out.println("1. Show all apartments");
                System.out.println("2. Search by address");
                System.out.println("3. Search by room count");
                System.out.println("4. Fill the table with 5 sample entries");
                System.out.println("5. Clear all entries");
                System.out.println("0. Exit");

                int choice = readIntInput("Enter your choice: ");

                switch (choice) {
                    case 1:
                        showAllApartments();
                        break;
                    case 2:
                        searchByAddress();
                        break;
                    case 3:
                        searchByRoomCount();
                        break;
                    case 4:
                        fillTableWithSampleEntries();
                        break;
                    case 5:
                        clearEntries();
                        break;
                    case 0:
                        return; // Exit the program
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to fill the table 'apts' with 5 sample entries
    private static void fillTableWithSampleEntries() throws SQLException {
        String insertEntrySQL = "INSERT INTO apts (area, address, square_ft, rooms_count, price) VALUES ";

        // Sample data for 5 entries
        double[] areas = {120.5, 80.0, 150.2, 100.0, 200.0};
        String[] addresses = {"123 Main St", "456 Park Ave", "789 Oak Rd", "101 Maple Ave", "555 Pine St"};
        double[] squareFt = {1500.0, 1000.0, 1800.0, 1200.0, 2500.0};
        int[] roomsCount = {3, 2, 4, 2, 5};
        double[] prices = {100000.0, 150000.0, 250000.0, 180000.0, 350000.0};

        for (int i = 0; i < areas.length; i++) {
            String entry = "(" + areas[i] + ", '" + addresses[i] + "', " + squareFt[i] + ", " + roomsCount[i] + ", " + prices[i] + ")";
            String insertSQL = insertEntrySQL + entry;

            Statement statement = conn.createStatement();
            statement.executeUpdate(insertSQL);
        }

        System.out.println("Filled the table 'apts' with 5 sample entries.");
    }

//     Method to clear all entries from the 'apts' table
    private static void clearEntries() throws SQLException {
        String clearSQL = "DELETE FROM apts";
        Statement statement = conn.createStatement();
        int deletedRows = statement.executeUpdate(clearSQL);
        // Reset primary key values (AUTO_INCREMENT)
        String resetPrimaryKeySQL = "ALTER TABLE apts AUTO_INCREMENT = 1";
        statement.executeUpdate(resetPrimaryKeySQL);
        System.out.println("Deleted " + deletedRows + " entries from the table 'apts'.");
    }

    // Helper method to read integer input from the user
    private static int readIntInput(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(message);
        return scanner.nextInt();
    }

    // Method to show all apartments in the 'apts' table
    private static void showAllApartments() throws SQLException {
        String selectAllSQL = "SELECT * FROM apts";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(selectAllSQL);

        // Print the results
        System.out.println("\nAll Apartments:");
        while (resultSet.next()) {
            System.out.println("ID: " + resultSet.getInt("id"));
            System.out.println("Area: " + resultSet.getDouble("area"));
            System.out.println("Address: " + resultSet.getString("address"));
            System.out.println("Square Feet: " + resultSet.getDouble("square_ft"));
            System.out.println("Rooms Count: " + resultSet.getInt("rooms_count"));
            System.out.println("Price: " + resultSet.getDouble("price"));
            System.out.println("----------------------");
        }
    }

    // Method to search for apartments by address
    private static void searchByAddress() throws SQLException {
        String searchAddress = readStringInput("Enter the address to search for: ");

        String searchSQL = "SELECT * FROM apts WHERE address LIKE ?";
        PreparedStatement preparedStatement = conn.prepareStatement(searchSQL);
        preparedStatement.setString(1, "%" + searchAddress + "%");
        ResultSet resultSet = preparedStatement.executeQuery();

        // Print the results
        System.out.println("\nApartments with address containing '" + searchAddress + "':");
        while (resultSet.next()) {
            System.out.println("ID: " + resultSet.getInt("id"));
            System.out.println("Area: " + resultSet.getDouble("area"));
            System.out.println("Address: " + resultSet.getString("address"));
            System.out.println("Square Feet: " + resultSet.getDouble("square_ft"));
            System.out.println("Rooms Count: " + resultSet.getInt("rooms_count"));
            System.out.println("Price: " + resultSet.getDouble("price"));
            System.out.println("----------------------");
        }
    }

    // Method to search for apartments by room count
    private static void searchByRoomCount() throws SQLException {
        int roomCount = readIntInput("Enter the room count to search for: ");

        String searchSQL = "SELECT * FROM apts WHERE rooms_count = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(searchSQL);
        preparedStatement.setInt(1, roomCount);
        ResultSet resultSet = preparedStatement.executeQuery();

        // Print the results
        System.out.println("\nApartments with " + roomCount + " rooms:");
        while (resultSet.next()) {
            System.out.println("ID: " + resultSet.getInt("id"));
            System.out.println("Area: " + resultSet.getDouble("area"));
            System.out.println("Address: " + resultSet.getString("address"));
            System.out.println("Square Feet: " + resultSet.getDouble("square_ft"));
            System.out.println("Rooms Count: " + resultSet.getInt("rooms_count"));
            System.out.println("Price: " + resultSet.getDouble("price"));
            System.out.println("----------------------");
        }
    }

    // Helper method to read string input from the user
    private static String readStringInput(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(message);
        return scanner.nextLine();
    }
}