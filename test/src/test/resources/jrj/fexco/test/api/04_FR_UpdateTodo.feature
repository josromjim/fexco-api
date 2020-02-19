Feature: Update Todo task
  Updating a Todo task

  Scenario: There is a call to the update end point with correct data
    Given The consumer sends a todo to update
    Then The system updates the todo to the database
    And sends a message with data updated to IoT Hub to deliver it through MQTT
    And returns the data updated

  Scenario: There is a call to the update end point with non-existent todo
    Given The consumer sends a updating todo with non-existent todo
    Then The system returns Not Found

  Scenario: There is a call to the update end point with wrong data
    Given The consumer sends a updating todo with wrong data
    Then The system returns an error