Feature: Login activity
  Perform login when a password is entered

  Scenario Outline: Entering an incorrect password gives an error
    Given I am viewing the login activity
    When I type <password> as password
    And I press the login button
    Then I should see an authentication error
    And I should stay in the LoginActivity

    Examples:
      | password |
      | 59505    |
      | 75757    |
      | 30572    |
