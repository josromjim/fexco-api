Feature: Get Todo task
  Getting of a Todo task by id

  Scenario: There is a call to the get by id end point with correct data
    Given The consumer sends a get request for Todo id
    Then The system gets the todo from the database
    And returns the data obtained

  Scenario: There is a call to add end point with wrong id
    Given The consumer sends a new todo with wrong id
    Then The system returns Not Found