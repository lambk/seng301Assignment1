Feature: Create an owner account
  Scenario: Adding new account
    Given I am connected to the owner database
    And The user with email "new.user@gmail.com" doesn't exist
    When I try to create an account with email "new.user@gmail.com"
    Then The database should contain a record under the email "new.user@gmail.com"

  Scenario: Adding existing account
    Given I am connected to the owner database
    And The user with email "joe@gmail.com" and last name "Bloggs" exists
    When I try to create an account with email "joe@gmail.com" and last name "Doe"
    Then The last name of the database record with email "joe@gmail.com" should be "Bloggs"

  Scenario: Not providing required fields
    Given I am connected to the owner database
    And The user with email "billy@gmail.com" doesn't exist
    When I try to create an account with email "billy@gmail.com" but provide a null first name
    Then The database shouldn't contain a record under the email "billy@gmail.com"