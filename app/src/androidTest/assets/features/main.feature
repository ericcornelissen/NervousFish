Feature: Main Activity
  All things related to the main activity screen

  Scenario: Log out from the main screen
    Given I am viewing the main activity
    When I click the back button in main and go to the LoginActivity
    And I verify that I want to log out
    Then I should go to the login activity after pressing back

  Scenario: Cancel logging out from the main screen
    Given I am viewing the main activity
    When I click the back button in main and go to the LoginActivity
    And I verify that I do not want to log out
    Then I should stay in the main activity after pressing back

  Scenario: Clicking the settings button
      Given I am viewing the main activity
      When I click the three dots in the main activity
      Then I should go to the settings screen
