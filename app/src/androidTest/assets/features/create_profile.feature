Feature: Create a profile
  When you create a profile

  Scenario: Clicking submit with nothing filled in
    Given I am viewing create profile activity
    When I click on the submit button
    And I click ok on the popup
    Then I should stay on the create profile activity
    And the name input field should become red
    And the password input field should become red
    And the repeat password input field should become red