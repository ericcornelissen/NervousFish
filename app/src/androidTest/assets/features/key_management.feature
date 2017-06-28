Feature: Key management activity
  Using the QR exchange activity

  Scenario: Press back
    Given I am viewing the key management activity
    When I press the key management back button
    Then I should return from the key management activity

  Scenario: Click keypair
    Given I have a foo keypair in my profile
    And I am viewing the key management activity
    When I click on the foo keypair
    Then I should see an alert dialog