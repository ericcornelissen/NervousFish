Feature: First time use
  When you use the app for the first time

  Scenario: Clicking the get started button
    Given I am viewing first time use activity
    When I click on the get started button
    Then I should go to the CreateProfileActivity