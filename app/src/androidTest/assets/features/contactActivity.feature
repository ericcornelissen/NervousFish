Feature: ContactPage
  Do several things on the contact activity page

  Scenario: Click on the back button
    Given I am viewing the contact activity
    When I press the back arrow
    Then I should go to the previous activity I visited

  Scenario: Click on the delete button delete contact
    Given I am viewing the contact activity
    When I press the delete button
    And I press that I am sure
    And I press on the OK button
    Then the current contact should be deleted
    And I should go to the previous activity I visited

  Scenario: Change the name of a contact
      Given I am viewing the contact activity
      When I press the contact name
      And I type a different name
      And I press on the save button
      Then the contact should be updated
      And I should see the new contact name