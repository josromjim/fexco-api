Feature: API Concurrency
  The API must support at least 50 concurrent users

  Scenario: The API is online
    Given The API is online on Azure App Services
    Then Azure App Services ensures the concurrency of the app with a load balancer and autoscaling