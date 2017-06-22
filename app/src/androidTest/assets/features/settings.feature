Feature: Settings Activity
  Using the settings of the application

  Scenario: Clicking the back button to go back to the main activity
    Given I am viewing the settings activity
    When I click the back button in the settings activity to go back to main
    Then I should go back to the main activity from settings

  Scenario: Click on general settings
    Given I am viewing the settings activity
    When I click on the general settings item
    Then I should go to the general settings fragment

  Scenario: Click on personal information
    Given I am viewing the settings activity
    When I click on the personal information item
    Then I should go to the personal information fragment