Feature: Reset UI
  Reseting UI state

  Scenario: The user resets the UI
    Given The user clicks on "Reset" button
    Then the task field is set to empty
    And editing mode is disabled