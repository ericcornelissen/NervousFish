Feature: WaitingForSlave
  Cancel the pairing with the slave as a master

  Scenario: Click on the cancel button
    Given I am viewing the waitingForSlave activity
    When I press the cancel button
    Then I should go to the MainActivity