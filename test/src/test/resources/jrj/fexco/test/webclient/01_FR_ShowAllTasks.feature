Feature: Show all tasks on page load
  The UI shall show all tasks when the page is loaded

  Scenario: The user opens the site
    Given The user opens the main site
    Then The system calls the API to load all tasks
    And shows all tasks

  Scenario: The user opens the site
    Given The user opens the main site
    When There are no tasks on the system
    And shows a message notifying there are no tasks