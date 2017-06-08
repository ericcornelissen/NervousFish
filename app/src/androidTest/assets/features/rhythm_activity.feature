Feature: RhythmActivity
  Tap a rhythm in the RhythmActivity

  Scenario: Tap a rhythm, stop, start recording and press done
    Given I am viewing the rhythm activity
    When I press the start recording button
    And I press the tap button a couple of times
    And I press the stop recording button
    And I press the done button
    Then I should go to the WaitActivity