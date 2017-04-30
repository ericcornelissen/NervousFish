Feature: Login
  Perform login when a password is entered


  Scenario Outline: Input and validate password
    Given I have a LoginActivity
    When I input password "<password>"
    And I press submit button
    Then I should <see> auth error

    Examples:
      | password   | see   |
      | 85685 | true  |
      | 12345  | false |
      | 23235  | true  |