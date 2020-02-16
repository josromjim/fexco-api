Feature: Real-time messages
  How to process IoT Messages

  Scenario: The system receives an "insert" message
    Given The IoT module receives an "insert" message with a task
    Then The web adds the task to the list of the UI

  Scenario: The system receives an "update" message
    Given The IoT module receives an "update" message with a task
    Then The web updates the task on the list of the UI
    
  Scenario: The system receives a "delete" message
    Given The IoT module receives a "delete" message with a task
    Then The web removes the task from the list of the UI