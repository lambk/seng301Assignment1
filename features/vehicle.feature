Feature: Registering a vehicle

  Scenario: Registering one or more vehicles
    Given I am connected to the vehicle database
    And The owner with email "kla98@uclive.ac.nz" exists
    And The vehicle with vin "IJK384" doesn't exist
    And The vehicle with vin "LJM897" doesn't exist
    When I try to register the vehicle with vin "IJK384" under email "kla98@uclive.ac.nz"
    And I try to register the vehicle with vin "LJM897" under email "kla98@uclive.ac.nz"
    Then The vehicle database should contain a record under vin "IJK384"
    And The vehicle database should contain a record under vin "LJM897"
    And Close the vehicle database

  Scenario: Registering a vehicle with an owner that doesn't exist
    Given I am connected to the vehicle database
    And the owner with email "frank.in.stine@gmail.com" doesn't exist
    And The vehicle with vin "YDH235" doesn't exist
    When I try to register the vehicle with vin "YDH235" under email "frank.in.stine@gmail.com"
    Then The vehicle database should not contain a record under vin "YDH235"
    And Close the vehicle database

  Scenario: Registering a vin that already exists
    Given I am connected to the vehicle database
    And The owner with email "thisisanemail@gmail.com" exists
    And The owner with email "abc.123@gmail.com" exists
    And The vehicle with vin "UHD837" and make "Toyota" exists under email "thisisanemail@gmail.com"
    When I try to register the vehicle with vin "UHD837" and make "Subaru" under email "abc.123@gmail.com"
    Then The vehicle with vin "UHD837" should have make "Toyota"
    And Close the vehicle database

  Scenario: Providing an invalid field
    Given I am connected to the vehicle database
    And The owner with email "some.email@somewebsite.com" exists
    And The vehicle with vin "DUH439" doesn't exist
    When I try to register the vehicle with vin "DUH439" under email "some.email@somewebsite.com", but provide fueltype "imagination"
    Then The vehicle database should not contain a record under vin "DUH439"
    And Close the vehicle database