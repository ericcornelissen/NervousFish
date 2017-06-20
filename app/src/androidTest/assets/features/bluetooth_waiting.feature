Feature: WaitingActivity
  The wait activity in the Bluetooth connection process

  Scenario: Click on the cancel button
    Given I am viewing the waiting for slave activity
    When I press the cancel waiting for slave button
    Then the wait activity should be finishing