Feature: Delete Todo task
  Deletion of a Todo task

  Scenario: There is a call to the delete end point with correct data
    Given The consumer sends a delete request for Todo id
    Then The system deletes the todo from the database
    And sends a message with data deleted to IoT Hub to deliver it through MQTT
    And returns the data deleted
    
	Scenario: There is a call to the delete end point with non-existent todo
    Given The consumer sends a delete todo with non-existent todo
    Then The system returns Not Found