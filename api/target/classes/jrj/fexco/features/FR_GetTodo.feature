Feature: Get Todo task
  Getting of a Todo task by id

  Scenario: There is a call to the get by id end point with correct data
    Given The consumer sends a get request for Todo id
    Then The system gets the todo from the database
    And returns the data obtained

  Scenario: There is a call to get end point with non-existent todo
    Given The consumer requests get with non-existent todo id
    Then The system returns Not Found