Feature: Login
  Perform login when a password is entered

  Scenario Outline: Input password and go to MainActivity or not
    Given I have a LoginActivity
    When I input password "<password>"
    And I press submit button
    Then I should stay in the LoginActivity

    Examples:
      | password |
      | 59505    |
      | 23235    |

  Scenario Outline: Check that wrong password gives error
      Given I have a LoginActivity
      When I input password "<password>"
      And I press submit button
      Then I should see an auth error

      Examples:
        | password |
        | 75757    |
        | 30572    |
