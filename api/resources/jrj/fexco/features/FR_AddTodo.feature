Feature: Add Todo task
  Addition of a new Todo

  Scenario: There is a call to the add end point with correct data
    Given The consumer sends a new todo
    Then The system adds the todo to the database
    And sends a message with data created to IoT Hub to deliver it through MQTT
    And returns the data created

  Scenario: There is a call to the add end point with wrong data
    Given The consumer sends a new todo with wrong data
    Then The system returns an error