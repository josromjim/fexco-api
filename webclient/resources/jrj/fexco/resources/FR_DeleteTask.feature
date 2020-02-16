Feature: Delete task
  Deletion of a task

  Scenario: The deletes a task
    Given The user clicks on a task's "Delete" button
    Then The system calls the API to delete the task
    And delete the task from the list