package sql;

import java.security.InvalidParameterException;
import java.sql.*;
import java.time.LocalDate;

public class SQLite {

    public ResultSet getOwnersByEmail(String email) {
        assert(email != null && email.length() > 0);
    }

    public void addOwner(String email, String firstNames, String lastName, String password) {
        if (email != null && email.length() > 0) new InvalidParameterException("Email is invalid (must be non-null and have length > 0)");
        if (firstNames != null && firstNames.length() > 0) throw new InvalidParameterException("Firstnames is invalid (must be non-null and have length > 0)");
        if (lastName != null && lastName.length() > 0) throw new InvalidParameterException("Lastname is invalid (must be non-null and have length > 0)");
        if (password != null && password.length() > 0) throw new InvalidParameterException("Password is invalid (must be non-null and have length > 0)");


    }

    public ResultSet getRegistrationsByVin(String vin) {

    }

    public void registerVehicle(String vin, String make, String model, String fuelType, String odometer, LocalDate firstRegistrationDate, LocalDate wofExpiryDate) {

    }

    public ResultSet selectAllInspections(Connection connection) throws SQLException{
        assert null != connection;
        System.out.println("Getting all inspections");
        PreparedStatement statement = connection.prepareStatement("select * from inspection");
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

    public ResultSet selectAllVehicles(Connection connection) throws SQLException{
        assert null != connection;
        System.out.println("Getting all vehicles");
        PreparedStatement statement = connection.prepareStatement("select * from vehicle");
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

    public ResultSet selectInspectionsForVehicle(Connection connection, String plate) throws SQLException {
        assert null != connection && null != plate;
        System.out.println("Getting inspections for vehicle: " + plate);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM inspection WHERE vehicle = ?");
        statement.setString(1, plate);
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

    public boolean checkUniqueInspection(Connection connection, String plate, int date) throws SQLException {
        ResultSet inspections = selectAllInspections(connection);
        while(inspections.next()) {
            if (inspections.getString("vehicle").equals(plate) && inspections.getInt("date") == date) {
                return false;
            }
        }
        return true;
    }

    public boolean checkUniquePlate(Connection connection, String plate) throws SQLException {
        ResultSet vehicles = selectAllVehicles(connection);
        while(vehicles.next()) {
            if (vehicles.getString("plate").equals(plate)) {
                return false;
            }
        }
        return true;
    }

    public void insertInspection(Connection connection, int date, String plate) throws
            SQLException {
        assert null != connection && 0 != date && null != plate;
        if (!checkUniqueInspection(connection, plate, date)) {
            System.out.println("Inspection for " + plate + " at " + date + " already exists in the database");
        } else {
            System.out.println("add a new inspection for date " + date + " of plate " + plate);
            // using wildcards ("?") to make the sql statement reusable and easier to read
            // as no type conversion are needed when passing parameters to insert in DB
            String insert = "insert into inspection(date, vehicle) values (?,?)";
            PreparedStatement statement = connection.prepareStatement(insert);
            // use indexes of wildcard ("?") starting from 1
            statement.setInt(1, date);
            statement.setString(2, plate);
            // print the result of the insert statement, 0 means nothing has been inserted
            System.out.println("Rows added: " + statement.executeUpdate());
        }
    }

    public void insertVehicle(Connection connection, String plate, String make, String model) throws SQLException {
        assert null != connection && null != plate && null != make && null != model;
        if (!checkUniquePlate(connection, plate)) {
            System.out.println(plate + " already exists in the database");
        } else {
            System.out.println("Adding vehicle: [" + plate + "] " + make + ", " + model);
            String insert = "INSERT INTO VEHICLE(plate, make, model) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insert);
            statement.setString(1, plate);
            statement.setString(2, make);
            statement.setString(3, model);
            System.out.println("Rows added: " + statement.executeUpdate());
        }
    }

    public void printInspection(ResultSet set) throws SQLException {
        assert null != set;
        // rows must be retrieved by names
        System.out.print(set.getString("id_inspection") + ", ");
        System.out.print(set.getInt("date") + ", ");
        // or by index (starts at index 1 for columns)
        System.out.print(set.getString(3));
        System.out.println();
    }

    public void printVehicle(ResultSet set) throws SQLException {
        assert null != set;
        System.out.println(set.getString("plate"));
        System.out.println(set.getString("make"));
        System.out.println(set.getString("model"));
        System.out.println();
    }

    public ResultSet getvehicle(Connection connection, String vehicle_plate) throws SQLException {
        assert null != connection && null != vehicle_plate;
        System.out.println("Getting vehicle with plate: " + vehicle_plate);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM VEHICLE WHERE plate = ?");
        statement.setString(1, vehicle_plate);
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

    public void updateVehicle(Connection connection, String vehicle_plate, String newModel) throws SQLException {
        assert null != connection && null != vehicle_plate && null != newModel;
        System.out.println("Setting model for vehicle with plate: " + vehicle_plate);
        PreparedStatement statement = connection.prepareStatement("UPDATE VEHICLE SET model=? WHERE plate=?");
        statement.setString(1, newModel);
        statement.setString(2, vehicle_plate);
        statement.executeUpdate();
    }

}
