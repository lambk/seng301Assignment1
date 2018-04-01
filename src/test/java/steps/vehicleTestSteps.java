package steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import sql.SQLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.LocalDate;

public class vehicleTestSteps {
    private SQLite db = new SQLite();
    private Connection connection;

    @Given("^I am connected to the vehicle database$")
    public void iAmConnectedToTheVehicleDatabase() throws Throwable {
        String url = "jdbc:sqlite:db.sqlite";
        connection = DriverManager.getConnection(url);
    }

    @And("^The vehicle with vin \"([^\"]*)\" doesn't exist$")
    public void theVehicleWithVinDoesnTExist(String vin) throws Throwable {
        db.unregisterVehicle(connection, vin);
    }

    @And("^The owner with email \"([^\"]*)\" exists$")
    public void theOwnerWithEmailExists(String email) throws Throwable {
        db.addOwner(connection, email, "first", "last", "password");
    }

    @And("^the owner with email \"([^\"]*)\" doesn't exist$")
    public void theOwnerWithEmailDoesnTExist(String email) throws Throwable {
        db.deleteOwner(connection, email);
    }

    @When("^I try to register the vehicle with vin \"([^\"]*)\" under email \"([^\"]*)\"$")
    public void iTryToRegisterTheVehicleWithVinUnderEmail(String vin, String email) throws Throwable {
        db.registerVehicle(connection, email, vin, "Toyota", "Celica", "MA", "petrol", 150000, LocalDate.of(2000, 1, 1), LocalDate.of(2019, 1, 1));
    }

    @Then("^The vehicle database should contain a record under vin \"([^\"]*)\"$")
    public void theVehicleDatabaseShouldContainARecordUnderVinAndEmail(String vin) throws Throwable {
        ResultSet rows = db.getRegistrationsByVin(connection, vin);
        Assert.assertTrue(rows.next());
    }


    @Then("^The vehicle database should not contain a record under vin \"([^\"]*)\"$")
    public void theVehicleDatabaseShouldNotContainARecordUnderVin(String vin) throws Throwable {
        ResultSet rows = db.getRegistrationsByVin(connection, vin);
        Assert.assertFalse(rows.next());
    }

    @And("^Close the vehicle database$")
    public void closeTheVehicleDatabase() throws Throwable {
        connection.close();
    }


    @And("^The vehicle with vin \"([^\"]*)\" and make \"([^\"]*)\" exists under email \"([^\"]*)\"$")
    public void theVehicleWithVinAndMakeExistsUnderEmail(String vin, String make, String email) throws Throwable {
        db.registerVehicle(connection, email, vin, make, "model", "MA", "diesel", 1, LocalDate.of(1990, 1, 1), LocalDate.of(2019, 2, 2));
    }

    @When("^I try to register the vehicle with vin \"([^\"]*)\" and make \"([^\"]*)\" under email \"([^\"]*)\"$")
    public void iTryToRegisterTheVehicleWithVinAndMakeUnderEmail(String vin, String make, String email) throws Throwable {
        db.registerVehicle(connection, email, vin, make, "model", "MA", "gas", 12302, LocalDate.of(1995, 1, 1), LocalDate.of(2018, 12, 2));
    }

    @Then("^The vehicle with vin \"([^\"]*)\" should have make \"([^\"]*)\"$")
    public void theVehicleWithVinShouldHaveMake(String vin, String make) throws Throwable {
        ResultSet rows = db.getRegistrationsByVin(connection, vin);
        if (!rows.next()) {
            throw new Exception("The vehicle with the provided vin doesn't exist");
        }
        Assert.assertEquals(make, rows.getString("make"));
    }

    @When("^I try to register the vehicle with vin \"([^\"]*)\" under email \"([^\"]*)\", but provide fueltype \"([^\"]*)\"$")
    public void iTryToRegisterTheVehicleWithVinUnderEmailButProvideFueltype(String vin, String email, String fuelType) throws Throwable {
        db.registerVehicle(connection, email, vin, "make", "model", "MA", fuelType, 239393, LocalDate.of(1992, 4, 4), LocalDate.of(2018, 7, 7));
    }
}
