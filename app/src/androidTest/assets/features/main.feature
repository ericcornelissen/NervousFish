Feature: Main Activity
  All things related to the main activity screen

  Scenario Outline: Log out from the main screen
    Given I am viewing the main activity
    When I click the back button
    And I verify that I want to log out
    Then I should go to the login activity

  Scenario Outline: Cancel logging out from the main screen
    Given I am viewing the main activity
    When I click the back button
    And I verify that I do not want to log out
    Then I should stay in the main activity
