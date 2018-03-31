package sql;

import java.sql.*;
import java.time.LocalDate;

public class SQLite {

    /**
     * Returns a set of records of owner accounts that have the provided email.
     * This will always be length 0 or 1 as email is the primary key
     * @param connection The SQL connection
     * @param email The email to search for
     * @return The ResultSet containing the matching account
     * @throws SQLException If an error occurs during a database action
     */
    public ResultSet getOwnersByEmail(Connection connection, String email) throws SQLException {
        assert(connection != null);
        assert(email != null && email.length() > 0);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM owner WHERE email=?");
        statement.setString(1, email);
        return statement.executeQuery();
    }

    public void addOwner(Connection connection, String email, String firstNames, String lastName, String password) throws SQLException {
        if (email == null || email.length() == 0) {
            System.out.println("Email is invalid (must be non-null and have length > 0)");
            return;
        }
        if (firstNames == null || firstNames.length() == 0) {
            System.out.println("First names are invalid (must be non-null and have length > 0)");
            return;
        }
        if (lastName == null || lastName.length() == 0) {
            System.out.println("Last name is invalid (must be non-null and have length > 0)");
            return;
        }
        if (password == null || password.length() == 0) {
            System.out.println("Password is invalid (must be non-null and have length > 0)");
            return;
        }

        ResultSet ownersWithEmail = getOwnersByEmail(connection, email);
        if (ownersWithEmail.next()) {
            System.out.println("Email is already in use");
            return;
        }

        PreparedStatement statement = connection.prepareStatement("INSERT INTO owner VALUES (?, ?, ?, ?)");
        statement.setString(1, email);
        statement.setString(2, firstNames);
        statement.setString(3, lastName);
        statement.setString(4, password);
        statement.executeUpdate();
        System.out.println("User added successfully");
    }

    public ResultSet getRegistrationsByVin(Connection connection, String vin) throws SQLException {
        assert(connection != null);
        assert(vin != null && vin.length() > 0);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM vehicle WHERE vin=?");
        statement.setString(1, vin);
        return statement.executeQuery();
    }

    public void registerVehicle(Connection connection, String email, String vin, String make, String model, String vehicleType, String fuelType, int odometer, LocalDate firstRegistrationDate, LocalDate wofExpiryDate) throws SQLException {
        if (email == null || email.length() == 0) {
            System.out.println("Email is invalid (must be non-null and have length > 0)");
            return;
        }
        if (vin == null || vin.length() == 0) {
            System.out.println("VIN is invalid (must be non-null and have length > 0");
            return;
        }
        if (make == null || make.length() == 0) {
            System.out.println("Make is invalid (must be non-null and have length > 0");
            return;
        }
        if (model == null || model.length() == 0) {
            System.out.println("Model is invalid (must be non-null and have length > 0");
            return;
        }
        String[] vehicleTypes = {"MA", "MB", "MC", "T", "O"};
        boolean vehicleTypeValid = false;
        for (String type : vehicleTypes) {
            if (vehicleType.toUpperCase().equals(type)) {
                vehicleTypeValid = true;
            }
        }
        if (!vehicleTypeValid) {
            System.out.println("Vehicle type must be either MA, MB, MC, T, or O");
            return;
        }
        String[] fuelTypes = {"diesel", "petrol", "electric", "gas", "other"};
        boolean fuelTypeValid = false;
        for (String type : fuelTypes) {
            if (fuelType.toLowerCase().equals(type)) {
                fuelTypeValid = true;
            }
        }
        if (!fuelTypeValid) {
            System.out.println("Fuel type must be either diesel, petrol, electric, gas, or other");
            return;
        }
        if (firstRegistrationDate.isAfter(LocalDate.now())) {
            System.out.println("First registration date must be in the past");
            return;
        }

        ResultSet ownersWithEmail = getOwnersByEmail(connection, email);
        if (!ownersWithEmail.next()) {
            System.out.println("No accounts exist under this email");
            return;
        }

        ResultSet vehiclesWithVin = getRegistrationsByVin(connection, vin.toUpperCase());
        if (vehiclesWithVin.next()) {
            System.out.println("VIN is already registered under a vehicle");
            return;
        }

        PreparedStatement statement = connection.prepareStatement("INSERT INTO vehicle VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, vin.toUpperCase());
        statement.setString(2, make);
        statement.setString(3, model);
        statement.setString(4, fuelType);
        statement.setInt(5, odometer);
        statement.setDate(6, Date.valueOf(firstRegistrationDate));
        statement.setDate(7, Date.valueOf(wofExpiryDate));
        statement.setString(8, email);
        statement.executeUpdate();
        System.out.println("Vehicle " + vin.toUpperCase() + " successfully registered");
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
