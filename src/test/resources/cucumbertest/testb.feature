@cucumber-test @P1
Feature: Feature File B

  Scenario: feature Two scenario One
    When feature Two scenario One step One
    And feature Two scenario One step Two
    Then feature Two scenario One step Three

  Scenario: feature Two scenario Two
    When feature Two scenario Two step One
    And feature Two scenario Two step Two
    Then feature Two scenario Two step Three

  @RunSequence
  Scenario: feature Two scenario Three
    When feature Two scenario Three step One
    And feature Two scenario Three step Two
    Then feature Two scenario Three step Three

  Scenario: feature Two scenario Four
    When feature Two scenario Four step One
    And feature Two scenario Four step Two
    Then feature Two scenario Four step Three