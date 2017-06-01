Feature: Contact activity
  Do several things on the contact activity page

  Scenario: Click on the back button
    Given I am viewing the contact activity
    When I press the back button in the contact activity
    Then I should go to the activity I visited before the contact activity

  Scenario: Click on the delete menu button delete contact
    Given I am viewing the contact activity
    When I open the contact activity menu
    And I select delete contact
    And I verify that I am sure I want to delete the contact
    And I confirm the contact is deleted
    Then I should go to the activity I visited before the contact activity
    And the contact should be deleted

  Scenario: Click on the edit button to go change contact activity
    Given I am viewing the contact activity
    When I open the contact activity menu
    And I select edit contact
    Then I should go to the change contact activity