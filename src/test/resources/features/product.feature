Feature: Product

  Scenario: Register a new product successfully
    When sending a valid product
    Then the product is successfully registered

  Scenario: Update a product successfully
    Given that a product has already been registered
    When sending a valid product update request
    Then the product is displayed successfully

  Scenario: Display a registered product successfully
    Given that a product has already been registered
    When searching for the product
    Then the product is displayed successfully

  Scenario: Display all registered products successfully
    Given that two products has already been registered
    When searching for all products
    Then the product are displayed successfully

  Scenario: Disable a product successfully
    Given that a product has already been registered
    When sending a request to disable the product
    Then the product is displayed successfully

  Scenario: Activate a product successfully
    Given that a product has already been registered
    When sending a request to activate the product
    Then the product is displayed successfully
