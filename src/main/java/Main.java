import sql.SQLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SQLite db = new SQLite();
        String db_url = "jdbc:sqlite:db.sqlite";
        try (Connection connection = DriverManager.getConnection(db_url)) {
            db.addOwner(connection, "test3@email.com", "Bob", "Doe", "hunter2");
            //db.registerVehicle(connection, "test@email.com", "ABC123", "Toyota", "Celica", "PEtrOl", 139000, LocalDate.of(2018, 1, 1), LocalDate.of(2018, 5, 5));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
