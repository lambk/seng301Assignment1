import sql.SQLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        SQLite db = new SQLite();
        String db_url = "jdbc:sqlite:db.sqlite";
        try (Connection connection = DriverManager.getConnection(db_url)) {
            System.out.println("Clearing the database (comment out to prevent this)...");
            db.clearData(connection);

            System.out.println("\nAdding a user...");
            db.addOwner(connection, "real.person@email.com", "Bob", "Doe", "donthackme");

            System.out.println("\nRegistering a vehicle under that user...");
            db.registerVehicle(connection, "real.person@email.com", "ABC123", "Toyota", "Celica", 1994, "MA","petrol", 139000, LocalDate.of(2018, 1, 1), LocalDate.of(2018, 5, 5));

            System.out.println("\nRegistering a vehicle under an email that doesn't have an account...");
            db.registerVehicle(connection, "not.an.owner@gmail.com", "ZUH384", "Subaru", "Legacy", 2000, "MA", "diesel", 239383, LocalDate.of(1996, 3, 3), LocalDate.of(2018, 6, 9));

            System.out.println("\nCreating another user...");
            db.addOwner(connection, "joe.bloggs@gmail.com", "Joe", "Bloggs", "qwerty");

            System.out.println("\nCreating a user with the email of an existing user (joe.bloggs@gmail.com)...");
            db.addOwner(connection, "joe.bloggs@gmail.com", "Bob", "Lastname", "password123");

            System.out.println("\nPrinting all owners...");
            db.printOwners(connection);

            System.out.println("\nRegistering a vehicle with the vin of an existing registration");
            db.registerVehicle(connection, "joe.bloggs@gmail.com", "ABC123", "Mazda", "Something", 2005,"MA","petrol", 148500, LocalDate.of(2010, 5, 5), LocalDate.of(2018, 3, 9));

            System.out.println("\nRegistering another vehicle under real.person@email.com");
            db.registerVehicle(connection, "real.person@email.com", "DYH837", "Toyota", "Supra", 1995, "MA","petrol", 45000, LocalDate.of(2001, 1, 8), LocalDate.of(2018, 3, 2));

            System.out.println("\nRegistering a trailer");
            db.registerVehicle(connection, "joe.bloggs@gmail.com", "T123", "Kea", "Hauler trailer", 2003, "T", null, 43129, LocalDate.of(2003, 2, 18), LocalDate.of(2018, 8, 28));

            System.out.println("\nPrinting all vehicle registrations...");
            db.printRegistrations(connection);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
