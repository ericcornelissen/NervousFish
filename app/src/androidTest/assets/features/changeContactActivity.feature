Feature: ContactPage
  Editing a contact on the change contact page

  Scenario: Change the name of a contact
      Given I am viewing the change contact activity
      When I press the contact name
      And I type a different name
      And I press on the save button
      Then the contact should be updated
      And I should go to the contact activity
      And I should see the new contact name