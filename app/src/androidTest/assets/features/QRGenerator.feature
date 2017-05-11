Feature: Generate QR code
    A QR code should be generated from a given string
    A QR code can be decoded to a string (a public key)

    Scenario Outline: Generate a QR code from a string and decode it to the same string
        Given   I encode a string to a QR code
        When    I decode the QR code
        Then    I should receive the same string