Feature: Rhythm verification
  Using the rhythm verification methods for Bluetooth

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

  Scenario Outline: Tapping enough times
    Given I am viewing the create rhythm activity
    When I tap the start button
    And I tap the screen <tap_count> times
    And I press the stop button
    And I press the done button
    Then I go to WaitActivity

    Examples:
      | tap_count |
      | 3         |
      | 10        |