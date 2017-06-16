Feature: QR exchange key activity
    Generates QR of the user's public key and scans other public keys

    Scenario: Press back
        Given I am viewing QRExchange activity
        When I press the back button
        Then I should return from the QRExchangeKeyActivity