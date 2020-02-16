Feature: API Availability
  There is a need to keep high availability for the API

  Scenario: The API is online
    Given The API is online on Azure App Services
    Then Microsoft Azure App Services ensures the availability of the app 24/7
    And Microsoft SQL ensures the data base availability
    And Microsoft IoT Hub ensures the MQTT availability