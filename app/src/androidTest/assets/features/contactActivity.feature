Feature: ContactPage
  Do several things on the contact activity page

  Scenario: Click on the back button
    Given I am viewing the contact activity
    When I press the back arrow
    Then I should go to the previous activity I visited

  Scenario: Click on the delete button get popup
    Given I am viewing the contact activity
    When I press the delete button
    Then I should get a popup asking if I am sure to delete the contact