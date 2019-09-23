Feature: Onboarding of Input Value Definitions

  Scenario: Happy Path - Onboard to monitoring, query monitoring and publish updates to clients.

    Given the current system time is "2018-08-01T00:00:00.000+08"
    And there is no existing input value definitions
    And the client has subscribed to topic subscription.v1.onboarding-service.input-value-definition.update-event.all.CREDIT

    When I sent the following onboarding request to monitoring
      """
          Test Payload
      """

    Then I should receive the following onboarding response from monitoring
      """
          Test Payload
      """
#    Then monitoring should have only the following input value definitions

    Then the client should receive the following updates
      """
          Test Payload
      """

    When I send the following input value definition query request to monitoring server
    """
        source_system_id: "CREDIT"
    """

    Then I should receive the following query response from monitoring
     """
        Test Payload
     """

    Given the current system time is "2018-08-01T00:01:00.000+08"

    When I sent the following onboarding request to monitoring
      """
          Test Payload
      """

    Then I should receive the following onboarding response from monitoring
      """
          Test Payload
      """

    Then the client should receive the following updates
      """
          Test Payload
      """


#    Comment text
#
#  Scenario: All invalid requests
#    #Missing TimeZone and InputValueType
#    Given I build the following requests
#      | ID | Description                           | Input Value Type |
#      | 1  | CREDIT                                | MONETARY_VALUE   |
#      | 2  | LIQUIDITY                             | NAU              |