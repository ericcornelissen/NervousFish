Feature: Settings Activity
  Tests for the Settings Activity

Scenario: Clicking the back button to go back to the main activity
    Given I am viewing the settings activity
    When I click the back button in the settings activity to go back to main
    Then I should go back to the main activity from settings