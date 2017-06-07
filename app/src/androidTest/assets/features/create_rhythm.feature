Feature: Create a rhythm
When you create a rhythm

  Scenario Outline: Tapping too few times
    Given I am viewing the create rhythm activity
    When I tap the start button
    And I tap the screen <tap_count> times
    And I press the stop button
    Then I get an error that I tapped not enough times

    Examples:
      | tap_count |
      | 0         |
      | 1         |
      | 2         |

  Scenario: Tapping enough times
    Given I am viewing the create rhythm activity
    When I tap the start button
    And I tap the screen 10 times
    And I press the stop button
    And I press the done button
    Then I go to WaitActivity