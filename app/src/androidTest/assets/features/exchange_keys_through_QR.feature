Feature: QR exchange key activity
    Generates QR of the user's public key and scans other public keys

    Scenario: Generate QR Code
        Given I am viewing QRExchange activity
        When I press the generate button
        Then I should see a popup with my qr code

    Scenario: Deconstruct QR code
        Given I am viewing the QRExchange activity
        When I press the scan button
        Then I should go to the Barcode Scanner app