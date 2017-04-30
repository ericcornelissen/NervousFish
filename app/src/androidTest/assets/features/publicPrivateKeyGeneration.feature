Feature: Public and private key generation
  As a user
  I want to have the app generate a public/private key pair when I use it for the first time
  So that I don't have to generate a pair myself

  Scenario Outline: First time use of the app
    Given I have a LoginActivity
    When I don't have public/private key pair yet
    Then the app should generate a public/private key pair


  Scenario Outline: Already used the app before
    Given I have a LoginActivity
    When I already have a public/private key pair
    Then nothing should happen