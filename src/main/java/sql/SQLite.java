package sql;

import java.sql.*;
import java.time.LocalDate;

public class SQLite {

    /**
     * Returns a set of records of owner accounts that have the provided email.
     * This will always be length 0 or 1 as email is the primary key
     *
     * @param connection The SQL connection
     * @param email      The email to search for
     * @return The ResultSet containing the matching account
     * @throws SQLException If an error occurs during a database action
     */
    public ResultSet getOwnersByEmail(Connection connection, String email) throws SQLException {
        assert (connection != null);
        assert (email != null && email.length() > 0);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM owner WHERE email=?");
        statement.setString(1, email);
        return statement.executeQuery();
    }

    /**
     * Adds an owner account to the database.
     * Includes validation of fields
     * All fields must be non-null and have length > 0
     * Attempting to add an account that has the same email as an existing account will display an error
     * and the account will not be added.
     *
     * @param connection The database connection
     * @param email      The email for the new account
     * @param firstNames The first names of the owner (One string comprised of space separated names)
     * @param lastName   The last name of the owner
     * @param password   The password for the new account
     * @throws SQLException If there is an error adding the account or checking the presence of an existing account with the provided email
     */
    public void addOwner(Connection connection, String email, String firstNames, String lastName, String password) throws SQLException {
        //Validate the fields
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

        //Add the account
        PreparedStatement statement = connection.prepareStatement("INSERT INTO owner VALUES (?, ?, ?, ?)");
        statement.setString(1, email);
        statement.setString(2, firstNames);
        statement.setString(3, lastName);
        statement.setString(4, password);
        statement.executeUpdate();
        System.out.println("User added successfully");
    }

    /**
     * Deletes the account with the provided email
     *
     * @param connection The connection to the database
     * @param email      The email of the account to delete
     * @throws SQLException If there was an error deleting from the database
     */
    public void deleteOwner(Connection connection, String email) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM owner WHERE email=?");
        statement.setString(1, email);
        statement.executeUpdate();
    }

    /**
     * Returns a set of records of vehicle registrations that have the provided vin.
     * This will always be length 0 or 1 as vin is the primary key
     *
     * @param connection The SQL connection
     * @param vin        The vin to search for
     * @return The ResultSet containing the matching account
     * @throws SQLException If an error occurs during a database action
     */
    public ResultSet getRegistrationsByVin(Connection connection, String vin) throws SQLException {
        assert (connection != null);
        assert (vin != null && vin.length() > 0);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM vehicle WHERE vin=?");
        statement.setString(1, vin);
        return statement.executeQuery();
    }

    /**
     * Adds a vehicle registration to the database.
     * Includes validation of fields
     * All fields must be non-null and have length > 0
     * Attempting to register a vehicle that has the same vin as an existing registration will display an error
     * and the vehicle will not be registered.
     * If the provided email is not an existing account the registration will not complete and an error will display
     * If the vehicleType is not an existing option (MA, MB, MC, T, or O) the vehicle will not be registered
     * If the fuelType is not an existing option (diesel, petrol, electric, gas, other) the vehicle will not be registered
     *
     * @param connection            The database connection
     * @param email                 The owner email for the registration
     * @param vin                   The VIN of the vehicle
     * @param make                  The make of the vehicle
     * @param model                 The model of the vehicle
     * @param vehicleType           The vehicle type (Must be MA, MB, MC, T, or O)
     * @param fuelType              The fuel type (Must be diesel, petrol, electric, gas, or other)
     * @param odometer              The odometer reading of the vehicle
     * @param firstRegistrationDate The LocalDate of when the vehicle was first registered in NZ
     * @param wofExpiryDate         The LocalDate of when the current WoF expires
     * @throws SQLException If there is an error registering the vehicle or checking the presence of an existing account or registration
     */
    public void registerVehicle(Connection connection, String email, String vin, String make, String model, String vehicleType, String fuelType, int odometer, LocalDate firstRegistrationDate, LocalDate wofExpiryDate) throws SQLException {
        //Validating the fields
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
        //Checking for valid vehicle type
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
        //Checking for valid fuel type
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

        //Registering the vehicle
        PreparedStatement statement = connection.prepareStatement("INSERT INTO vehicle VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, vin.toUpperCase());
        statement.setString(2, make);
        statement.setString(3, model);
        statement.setString(4, vehicleType);
        statement.setString(5, fuelType);
        statement.setInt(6, odometer);
        statement.setDate(7, Date.valueOf(firstRegistrationDate));
        statement.setDate(8, Date.valueOf(wofExpiryDate));
        statement.setString(9, email);
        statement.executeUpdate();
        System.out.println("Vehicle " + vin.toUpperCase() + " successfully registered");
    }

    /**
     * Unregisters the vehicle with the provided vin
     *
     * @param connection The connection to the database
     * @param vin        The vin of the vehicle to unregister
     * @throws SQLException If there was an error deleting the record from the database
     */
    public void unregisterVehicle(Connection connection, String vin) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM vehicle WHERE vin=?");
        statement.setString(1, vin);
        statement.executeUpdate();
    }
}
