Feature: Entry Activity
  Testing if EntryActivity goes to the welcome or to the main screen

  Scenario: First time using the app
    Given I am using the app for the first time
    When I wait a very short time
    Then I go to WelcomeActivity

# We cannot test the second scenario: that the app goes to LoginActivity, because EntryActivity can only be opened once
# It is possible to open it twice by pressing back -> exiting the application, but espresso does not allow that unfortunately
