Feature: First time use of the app
  Using the app for the first time

  Scenario: Clicking the get started button
    Given I am viewing welcome activity
    When I click on the get started button
    Then I should go to the CreateProfileActivity