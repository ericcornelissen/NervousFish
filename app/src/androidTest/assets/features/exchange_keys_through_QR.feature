Feature: QR exchange key activity
    Generates QR of the user's public key and scans other public keys

    Scenario: Generate QR Code
        Given I am viewing QRExchange activity
        When I press the generate button
        Then I should see a popup with my qr code

    Scenario: Press back button
        Given I am viewing QRExchange activity
        When I press the back button
        Then I should return from the QRExchangeKeyActivity