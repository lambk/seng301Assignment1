import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sql.SQLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class vehicleTest {
    private SQLite db = new SQLite();
    private Connection connection;

    @Before
    public void setup() throws SQLException {
        String url = "jdbc:sqlite:db.sqlite";
        connection = DriverManager.getConnection(url);
    }

    @Test
    public void testIncorrectVehicleType() throws SQLException {
        db.addOwner(connection, "test@gmail.com", "first", "last", "password");
        //Making sure the vehicle doesn't exist already
        db.unregisterVehicle(connection, "YFH837");
        db.registerVehicle(connection, "test@gmail.com", "YFH837", "Make", "Model", 1993, "SomeVehicle", "petrol", 29393, LocalDate.of(1990, 1, 1), LocalDate.of(2018,7,4));
        //Checking that the vehicle didn't successfully register
        ResultSet rows = db.getRegistrationsByVin(connection, "YFH837");
        Assert.assertFalse(rows.next());
    }

    @Test
    public void testCorrectVehicleAndFuelType() throws SQLException {
        db.addOwner(connection, "test@gmail.com", "first", "last", "password");
        //Making sure the vehicle doesn't exist already
        db.unregisterVehicle(connection, "YFH837");
        db.registerVehicle(connection, "test@gmail.com", "YFH837", "Make", "Model", 1993, "MC", "petrol", 29393, LocalDate.of(1990, 1, 1), LocalDate.of(2018,7,4));
        //Checking that the vehicle didn't successfully register
        ResultSet rows = db.getRegistrationsByVin(connection, "YFH837");
        Assert.assertTrue(rows.next());
    }

    @Test
    public void testIncorrectFuelTypeForVehicle() throws SQLException {
        db.addOwner(connection, "test@gmail.com", "first", "last", "password");
        //Making sure the vehicle doesn't exist already
        db.unregisterVehicle(connection, "YFH837");
        db.registerVehicle(connection, "test@gmail.com", "YFH837", "Make", "Model", 1993, "MC", "someFuelType", 29393, LocalDate.of(1990, 1, 1), LocalDate.of(2018,7,4));
        //Checking that the vehicle didn't successfully register
        ResultSet rows = db.getRegistrationsByVin(connection, "YFH837");
        Assert.assertFalse(rows.next());
    }

    @Test
    public void testIncorrectFuelTypeButOnTrailer() throws SQLException {
        db.addOwner(connection, "test@gmail.com", "first", "last", "password");
        //Making sure the vehicle doesn't exist already
        db.unregisterVehicle(connection, "YFH837");
        db.registerVehicle(connection, "test@gmail.com", "YFH837", "Make", "Model", 1993, "T", "someFuelType", 29393, LocalDate.of(1990, 1, 1), LocalDate.of(2018,7,4));
        //Checking that the vehicle didn't successfully register
        ResultSet rows = db.getRegistrationsByVin(connection, "YFH837");
        Assert.assertTrue(rows.next());
    }

    @Test
    public void testNullFuelTypeForTrailer() throws SQLException {
        db.addOwner(connection, "test@gmail.com", "first", "last", "password");
        //Making sure the vehicle doesn't exist already
        db.unregisterVehicle(connection, "YFH837");
        db.registerVehicle(connection, "test@gmail.com", "YFH837", "Make", "Model", 1993, "T", null, 29393, LocalDate.of(1990, 1, 1), LocalDate.of(2018,7,4));
        //Checking that the vehicle didn't successfully register
        ResultSet rows = db.getRegistrationsByVin(connection, "YFH837");
        Assert.assertTrue(rows.next());
    }

    @Test
    public void testRegistrationAfterCurrentDate() throws SQLException {
        db.addOwner(connection, "test@gmail.com", "first", "last", "password");
        //Making sure the vehicle doesn't exist already
        db.unregisterVehicle(connection, "YFH837");
        db.registerVehicle(connection, "test@gmail.com", "YFH837", "Make", "Model", 1993, "MC", "petrol", 29393, LocalDate.of(2090, 1, 1), LocalDate.of(2018,7,4));
        //Checking that the vehicle didn't successfully register
        ResultSet rows = db.getRegistrationsByVin(connection, "YFH837");
        Assert.assertFalse(rows.next());
    }

    @After
    public void close() throws SQLException {
        connection.close();
    }
}
