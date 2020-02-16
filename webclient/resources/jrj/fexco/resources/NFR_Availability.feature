Feature: Web Availability
  There is a need to keep high availability for the Web site

  Scenario: The Web is online
    Given The Web is online on Azure App Services
    Then Microsoft Azure App Services ensures the availability of the app 24/7