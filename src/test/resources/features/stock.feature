Feature: Stock

  Scenario: Update a product stock successfully
    Given that a product has already been registered with a valid stock
    When sending a valid product stock update request
    Then the product stock is displayed successfully
