Feature: Add new task
  Addition of a new Task

  Scenario: The user adds a new task
    Given The user writes a task in the field
    When the user submits the task clicking
    Then The system calls the API to store the task
    And adds the task created to the list

  Scenario: The user adds a new task without text
    Given The user does not write anything in the field
    And the field is empty
    When the user submits the field
    Then the web notify the user to write the task before submitting