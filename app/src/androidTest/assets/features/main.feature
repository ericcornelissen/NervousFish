Feature: Main Activity
  All things related to the main activity screen

  Scenario Outline: Log out from the main screen
    Given I am viewing the main activity
    When I click the back button in main and go to the LoginActivity
    And I verify that I want to log out
    Then I should go to the login activity after pressing back

  Scenario Outline: Cancel logging out from the main screen
    Given I am viewing the main activity
    When I click the back button in main and go to the LoginActivity
    And I verify that I do not want to log out
    Then I should stay in the main activity after pressing back

  Scenario Outline: Clicking the QR button
      Given I am viewing the main activity
      When I click open buttons with the plus
      And I click the button with the QR icon
      Then I should go to the QR activity from main
