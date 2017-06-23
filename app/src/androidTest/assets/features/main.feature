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
    Then I should stay in the main activity from the main activity

  Scenario Outline: Clicking on a contact in the list of contacts
    Given there is a contact with the name <name> in the database
    And I am viewing the main activity
    When I click on the contact with the name <name>
    Then I should go to the contact activity from main

    Examples:
      | name     |
      | Henk     |
      | Zoidberg |
      | Dr. Evil |

  Scenario: Clicking the Bluetooth button
    Given I am viewing the main activity
    When I click on the new connection button
    And I click the button with the Bluetooth icon
    Then the app shouldn't crash because of Bluetooth

  Scenario: Clicking the Bluetooth button label
    Given I am viewing the main activity
    When I click on the new connection button
    And I click the button with the Bluetooth text label
    Then the app shouldn't crash because of Bluetooth

  Scenario: Clicking the NFC button
    Given I am viewing the main activity
    When I click on the new connection button
    And I click the button with the NFC icon
    Then I should go to the NFC activity from main

  Scenario: Clicking the NFC button label
    Given I am viewing the main activity
    When I click on the new connection button
    And I click the button with the NFC text label
    Then I should go to the NFC activity from main

  Scenario: Clicking the QR button
    Given I am viewing the main activity
    When I click on the new connection button
    And I click the button with the QR icon
    Then I should go to the QR activity from main

  Scenario: Clicking the QR button label
    Given I am viewing the main activity
    When I click on the new connection button
    And I click the button with the QR text label
    Then I should go to the QR activity from main

  Scenario: Clicking the settings button
    Given I am viewing the main activity
    When I click the three dots in the main activity
    Then I should go to the settings screen

  Scenario: Clicking the sort button
    Given I am viewing the main activity
    When I click the sort button in the main activity
    Then The app shouldn't crash

  Scenario: Clicking the sort button with different key types
    Given I am viewing the main activity
    When There are contacts with different keys in the database
    And I click the sort button in the main activity
    Then The app shouldn't crash
