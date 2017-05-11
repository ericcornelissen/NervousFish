Feature: start Bluetooth Connection activity
    As a user I want to access my bluetooth features with one button press from the main activity

    Scenario Outline: Press the connect button and go to the BluetoothConnectionActivity
        Given I have MainActivity
        When I press the connect button
        Then I go to the BluetoothConnectionActivity
