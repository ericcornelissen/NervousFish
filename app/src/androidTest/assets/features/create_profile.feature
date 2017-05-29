Feature: Create a profile
  When you create a profile

  Scenario: Clicking submit with nothing filled in
    Given I am viewing the create profile activity
    When I click on the submit profile button
    And I click ok on the popup with a warning about creating an profile
    Then I should stay on the create profile activity
    And the name input field should become red
    And the password input field should become red
    And the repeat password input field should become red

  Scenario Outline: Clicking submit with valid input
      Given I am viewing the create profile activity
      And there are no profiles in the database
      When I enter a valid <name> as name
      And I enter a valid <password> as password
      And I enter a valid repeat <password> as repeat password
      And I click on the submit profile button
    And I click ok on the popup with a warning about creating an profile
      Then I should go to the login activity
      And the profile with <name> should be saved in the database

      Examples:
         | name     | password      |
         | Jaap     | 5950577       |
         | Cl3nr0€k | ^&*)(_-34-    |
         | p        | apasswordd    |

  Scenario Outline: Password not longer than 6 characters
    Given I am viewing the create profile activity
    When I enter a valid <name> as name
    And I enter a <password> with a length smaller than 6 characters
    And I enter a valid repeat <password> as repeat password
    And I click on the submit profile button
    And I click ok on the popup with a warning about creating an profile
    Then I should stay on the create profile activity
    And the password input field should become red

    Examples:
         | name         | password  |
         | Drake        | a         |
         | CJ           | ^&*)(     |
         | Darth Vader  | 6782      |

  Scenario Outline: Password and repeat password not the same
    Given I am viewing the create profile activity
    When I enter a valid <name> as name
    And I enter a valid <password> as password
    And I enter a different <repeatpassword> than the password field
    And I click on the submit profile button
    And I click ok on the popup with a warning about creating an profile
    Then I should stay on the create profile activity
    And the password input field should become red
    And the repeat password input field should become red

    Examples:
     | name         | password  | repeatpassword    |
     | Drake        | a         | b                 |
     | CJ           | ^&*)(     | NetAnders         |
     | Darth Vader  | 6782      | 4578              |