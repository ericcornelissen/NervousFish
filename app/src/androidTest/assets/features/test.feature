Feature: Login
  Perform login on email and password are inputted


  Scenario Outline: Input email and password in correct format
    Given I have a LoginActivity
    When I input password "<password>"
    And I press submit button
    Then I should <see> auth error

    Examples:
      | password   | see   |
      | 85685 | true  |
      | 12345  | false |
      | 23235  | true  |