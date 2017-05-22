Feature: ContactPage
  Do several things on the contact activity page

  Scenario: Click on the back button
    Given I am viewing the contact activity
    When I press the back arrow
    Then I should go to the previous activity I visited

  Scenario: Click on the delete menu button delete contact
    Given I am viewing the contact activity
    When I press the menu
    And I press delete
    And I press that I am sure
    And I press on the OK button
    Then the current contact should be deleted
    And I should go to the previous activity I visited

  Scenario: Click on the edit button to go change contact activity
    Given I am viewing the contact activity
    When I press the menu
    And I press edit
    Then I should go to the change contact activity