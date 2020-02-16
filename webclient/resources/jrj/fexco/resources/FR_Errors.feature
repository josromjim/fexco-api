Feature: Show error on UI
  How to sho error on UI

  Scenario: An error ocurred
    Given There is an error between server and view
    Then the UI shows a message detailing the error