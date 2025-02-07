Feature: Load

  Scenario: Load a product csv file from endpoint
    Given that a csv file is available and valid
    When sending a request to load the file
    Then the file is loaded successfully

  Scenario: Schedule the load of a product csv file
    Given that a csv file is available and valid
    When scheduling the load of the file
    Then the file is scheduled to be loaded

  Scenario: Cancel the load of a product csv file Scheduled
    Given that a csv file is available and valid
    And scheduling the load of the file
    When cancelling the load of the file
    Then the file is not loaded