Feature: start Bluetooth Connection activity
  As a user I want to access my bluetooth features with one button press from the main activity

  Scenario Outline: Press the bluetooth button and go to the BluetoothConnectionActivity
    Given I have MainActivity
    When I press the pairing button
    And I press the bluetooth button
    Then I go to the BluetoothConnectionActivity

  Scenario Outline: Press the nfc button and go to the BluetoothConnectionActivity
    Given I have MainActivity
    When I press the pairing button
    And I press the NFC button
    Then I go to the NFCActivity

  Scenario Outline: Press the qr button and go to the BluetoothConnectionActivity
    Given I have MainActivity
    When I press the pairing button
    And I press the QR button
    Then I go to the QRActivity