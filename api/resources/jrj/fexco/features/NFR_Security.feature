Feature: API Security
  The API must be secured for trusted consumers

  Scenario: The API is online
    Given The API is online on Azure App Services
    Then The API must have a Token based security mechanism