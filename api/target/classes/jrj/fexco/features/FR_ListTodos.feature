Feature: Get All Todo tasks
  Getting of a all Todo tasks

  Scenario: There is a call to the get all end point
    Given The consumer sends a get all request
    Then The system gets all todos from the database
    And returns the data obtained