Feature: VisualVerification
  Do several things on the VisualVerificationActivity

  Scenario Outline: Fill out a correct pattern
    Given I am viewing the visual verification activity
    When I press button <first_button>
    And I press button <second_button>
    And I press button <third_button>
    And I press button <fourth_button>
    And I press button <fifth_button>
    Then I leave the visual verification activity

    Examples:
      | first_button  | second_button | third_button | fourth_button | fifth_button |
      | 1             | 3             | 2            | 5             | 9            |
      | 13            | 1             | 3            | 10            | 1            |
      | 2             | 4             | 2            | 4             | 2            |
      | 2             | 15            | 7            | 14            | 8            |