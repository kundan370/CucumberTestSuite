@cucumber-test
Feature: Feature File C

  Scenario: feature Three scenario One
    When feature Three scenario One step One
    And feature Three scenario One step Two
    Then feature Three scenario One step Three

  Scenario: feature Three scenario Two
    When feature Three scenario Two step One
    And feature Three scenario Two step Two
    Then feature Three scenario Two step Three

  @RunSequence
  Scenario: feature Three scenario Three
    When feature Three scenario Three step One
    And feature Three scenario Three step Two
    Then feature Three scenario Three step Three

  @RunSequence
  Scenario: feature Three scenario Four
    When feature Three scenario Four step One
    And feature Three scenario Four step Two
    Then feature Three scenario Four step Three