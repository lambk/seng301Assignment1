package steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import sql.SQLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class ownerTestSteps {
    private SQLite db = new SQLite();
    private Connection connection;

    @Given("^I am connected to the owner database$")
    public void iAmConnectedToTheOwnerDatabase() throws Throwable {
        String url = "jdbc:sqlite:db.sqlite";
        connection = DriverManager.getConnection(url);
    }

    @And("^The user with email \"([^\"]*)\" doesn't exist$")
    public void theUserWithEmailDoesnTExist(String email) throws Throwable {
        db.deleteOwner(connection, email);
    }

    @And("^The user with email \"([^\"]*)\" and last name \"([^\"]*)\" exists$")
    public void theUserWithEmailAndLastNameExists(String email, String lastName) throws Throwable {
        ResultSet rows = db.getOwnersByEmail(connection, email);
        if (!rows.next()){
            db.addOwner(connection, email, "Joe", lastName, "qwerty");
            rows = db.getOwnersByEmail(connection, email);
            rows.next();
        }
        Assert.assertTrue(rows.getString("email").equals(email) && rows.getString("last_name").equals(lastName));
    }

    @When("^I try to create an account with email \"([^\"]*)\"$")
    public void iTryToCreateAnAccountWithEmail(String email) throws Throwable {
        db.addOwner(connection, email, "Fred", "Flintstone", "noEngine");
    }

    @Then("^The database should contain a record under the email \"([^\"]*)\"$")
    public void theDatabaseShouldContainARecordUnderTheEmail(String email) throws Throwable {
        ResultSet rows = db.getOwnersByEmail(connection, email);
        Assert.assertTrue((rows).next());
    }

    @When("^I try to create an account with email \"([^\"]*)\" and last name \"([^\"]*)\"$")
    public void iTryToCreateAnAccountWithEmailAndLastName(String email, String lastName) throws Throwable {
        db.addOwner(connection, email, "Joe", lastName, "qwerty");
    }

    @Then("^The last name of the database record with email \"([^\"]*)\" should be \"([^\"]*)\"$")
    public void theLastNameOfTheDatabaseRecordWithEmailShouldBe(String email, String lastName) throws Throwable {
        ResultSet rows = db.getOwnersByEmail(connection, email);
        Assert.assertTrue(rows.getString("email").equals(email) && rows.getString("last_name").equals(lastName));
    }

    @When("^I try to create an account with email \"([^\"]*)\" but provide a null first name$")
    public void iTryToCreateAnAccountWithEmailButProvideANullFirstName(String email) throws Throwable {
        db.addOwner(connection, email, null, "Smith", "my_password");
    }

    @Then("^The database shouldn't contain a record under the email \"([^\"]*)\"$")
    public void theDatabaseShouldnTContainARecordUnderTheEmail(String email) throws Throwable {
        ResultSet rows = db.getOwnersByEmail(connection, email);
        Assert.assertFalse(rows.next());
    }

    @And("^Close the owner database$")
    public void closeTheOwnerDatabase() throws Throwable {
        connection.close();
    }
}
