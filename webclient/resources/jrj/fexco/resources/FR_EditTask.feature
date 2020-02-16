Feature: Edit a task
  Edition of a Task

  Scenario: The user edits a task
    Given The user clicks on "Update" button
    And the UI changes to "edit" mode
    When the user clicks on "Save" button
    Then The system calls the API to update the task
    And updates the task on the list

  Scenario: The user updates a task without text
    Given The UI is in "edit" mode
    And the user does not write anything in the field
    And the field is empty
    When the user submits the field
    Then the web notify the user to write the task before submitting