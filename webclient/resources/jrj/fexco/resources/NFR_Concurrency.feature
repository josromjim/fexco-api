Feature: Web Concurrency
  The Web must support at least 50 concurrent users

  Scenario: The Web is online
    Given The Web is online on Azure App Services
    Then Azure App Services ensures the concurrency of the app with a load balancer and autoscaling