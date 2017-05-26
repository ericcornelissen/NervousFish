Feature: WaitingForSlave
  Cancel the pairing with the slave as a master

  Scenario: Click on the cancel button
    Given I am viewing the waiting for slave activity
    When I press the cancel waiting for slave button
    Then I should go to the main activity