Feature: ChangeContact
  Editing a contact on the change contact page

  Scenario Outline: Change the name of a contact
      Given I am viewing the change contact activity
      When I press the contact name
      And I type a "<differentname>"
      And I close the keyboard
      And I press on the save button
      Then the contact should be updated
      And I should go to the previous activity I visited

      Examples:
        | differentname  |
        | OtherName  |
        | 6j206j206j20  |
        | @&*#%*$(^(  |

  Scenario Outline: Change the name of a contact to an invalid one
        Given I am viewing the change contact activity
        When I press the contact name
        And I type a "<differentname>"
        And I close the keyboard
        And I press on the save button
        And I press confirm on the popup
        Then I should stay on the page

        Examples:
          | differentname  |
          |   |

  Scenario: Press the back button after doing nothing
        Given I am viewing the change contact activity
        When I press the back button
        Then I should go to the previous activity I visited

  Scenario: Press the back button after changing something and go back
        Given I am viewing the change contact activity
        When I change the name
        And I close the keyboard
        And I press the back button
        And I press yes go back on the popup
        Then I should go to the previous activity