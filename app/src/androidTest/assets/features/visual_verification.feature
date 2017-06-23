Feature: Visual verification
  Using the visual verification method for Bluetooth

  Scenario Outline: Fill out a correct pattern
    Given I am viewing the visual verification activity
    When I press button <first_button> in the verification grid
    And I press button <second_button> in the verification grid
    And I press button <third_button> in the verification grid
    And I press button <fourth_button> in the verification grid
    And I press button <fifth_button> in the verification grid
    And I press button <sixth_button> in the verification grid
    Then I leave the visual verification activity

    Examples:
      | first_button  | second_button | third_button | fourth_button | fifth_button | sixth_button |
      | 1             | 3             | 2            | 5             | 9            | 10           |
      | 9             | 1             | 3            | 10            | 1            | 1            |
      | 2             | 4             | 2            | 4             | 2            | 5            |
      | 2             | 10            | 7            | 11            | 8            | 4            |