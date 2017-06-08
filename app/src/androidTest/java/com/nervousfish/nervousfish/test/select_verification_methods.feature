Feature: Select verification method
  Scenario: Select the rhythm verification method
    Given I am viewing the select verification method activity
    When I press the tab a rhythm button
    Then I should go to the rhythm activity to provide a pattern

  Scenario: Select the visual verification method
    Given I am viewing the select verification method activity
    When I press the visual pattern button
    Then I should go to the visual pattern activity to provide a pattern