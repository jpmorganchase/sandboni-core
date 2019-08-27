Feature: Onboarding of Input Value Definitions

  Scenario: Happy Path - Onboard to monitoring, query monitoring and publish updates to clients.

    Given the current system time is "2018-08-01T00:00:00.000+08"
    And there is no existing input value definitions
    And the client has subscribed to topic subscription.v1.onboarding-service.input-value-definition.update-event.all.CREDIT

    When I sent the following onboarding request to monitoring
      """
          unique_ref_id: "1"
          input_value_definitions {
              source_system_id: "CREDIT"
              source_id: "CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll"
              description: "CFU GrossBalanceAll in JPMCB New York"
              input_value_type: MONETARY_VALUE
              time_config {
                  time_zone: "America/New_York"
                  holiday_calendar_code: "NAU"
                  start_of_day_time_offset: 0
                  end_of_day_time_offset: 0
                  rollover_time_offset: 0
              }
              dimensions {
                  name: "clientName"
              }
              additional_attributes {
                  key: "legalEntityCode"
                  value: "317"
              }
              additional_attributes {
                  key: "currency"
                  value: "USD"
              }
          }
      """

    Then I should receive the following onboarding response from monitoring
      """
          status  {
              responseStatusCode: 200
              responseStatusDescription: "1 input value definitions onboarded successfully."
          }
          description: "1 input value definitions onboarded successfully."
      """
#    Then monitoring should have only the following input value definitions

    Then the client should receive the following updates
      """
          event_timestamp: "2018-08-01T00:00:00.000+08:00"
          all_input_value_definitions {
              source_system_id: "CREDIT"
              source_id: "CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll"
              description: "CFU GrossBalanceAll in JPMCB New York"
              input_value_type: MONETARY_VALUE
              time_config {
                  time_zone: "America/New_York"
                  holiday_calendar_code: "NAU"
                  start_of_day_time_offset: 0
                  end_of_day_time_offset: 0
                  rollover_time_offset: 0
              }
              dimensions {
                  name: "clientName"
              }
              additional_attributes {
                  key: "currency"
                  value: "USD"
              }
              additional_attributes {
                  key: "legalEntityCode"
                  value: "317"
              }
          }
      """

    When I send the following input value definition query request to monitoring server
    """
        source_system_id: "CREDIT"
    """

    Then I should receive the following query response from monitoring
     """
        status {
            responseStatusCode: 200
            responseStatusDescription: "1 input value definition(s) found"
        }
        input_value_definitions {
            source_system_id: "CREDIT"
            source_id: "CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll"
            description: "CFU GrossBalanceAll in JPMCB New York"
            input_value_type: MONETARY_VALUE
            time_config {
                time_zone: "America/New_York"
                holiday_calendar_code: "NAU"
                start_of_day_time_offset: 0
                end_of_day_time_offset: 0
                rollover_time_offset: 0
            }
            dimensions {
                name: "clientName"
            }
            additional_attributes {
                key: "currency"
                value: "USD"
            }
            additional_attributes {
                key: "legalEntityCode"
                value: "317"
            }
        }
     """

    Given the current system time is "2018-08-01T00:01:00.000+08"

    When I sent the following onboarding request to monitoring
      """
          unique_ref_id: "2"
          input_value_definitions {
              source_system_id: "CREDIT"
              source_id: "CRD.All.LE_937.CreditFamilyUsage-by-GrossBalanceAll"
              description: "CFU GrossBalanceAll in JPMCB London"
              input_value_type: MONETARY_VALUE
              time_config {
                  time_zone: "America/New_York"
                  holiday_calendar_code: "NAU"
                  start_of_day_time_offset: 0
                  end_of_day_time_offset: 0
                  rollover_time_offset: 0
              }
              dimensions {
                  name: "clientName"
              }
              additional_attributes {
                  key: "currency"
                  value: "USD"
              }
              additional_attributes {
                  key: "legalEntityCode"
                  value: "937"
              }
          }
      """

    Then I should receive the following onboarding response from monitoring
      """
          status  {
              responseStatusCode: 200
              responseStatusDescription: "1 input value definitions onboarded successfully."
          }
          description: "1 input value definitions onboarded successfully."
      """

    Then the client should receive the following updates
      """
          event_timestamp: "2018-08-01T00:01:00.000+08:00"
          all_input_value_definitions {
              source_system_id: "CREDIT"
              source_id: "CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll"
              description: "CFU GrossBalanceAll in JPMCB New York"
              input_value_type: MONETARY_VALUE
              time_config {
                  time_zone: "America/New_York"
                  holiday_calendar_code: "NAU"
                  start_of_day_time_offset: 0
                  end_of_day_time_offset: 0
                  rollover_time_offset: 0
              }
              dimensions {
                  name: "clientName"
              }
              additional_attributes {
                  key: "currency"
                  value: "USD"
              }
              additional_attributes {
                  key: "legalEntityCode"
                  value: "317"
              }
          }
          all_input_value_definitions {
              source_system_id: "CREDIT"
              source_id: "CRD.All.LE_937.CreditFamilyUsage-by-GrossBalanceAll"
              description: "CFU GrossBalanceAll in JPMCB London"
              input_value_type: MONETARY_VALUE
              time_config {
                  time_zone: "America/New_York"
                  holiday_calendar_code: "NAU"
                  start_of_day_time_offset: 0
                  end_of_day_time_offset: 0
                  rollover_time_offset: 0
              }
              dimensions {
                  name: "clientName"
              }
              additional_attributes {
                  key: "currency"
                  value: "USD"
              }
              additional_attributes {
                  key: "legalEntityCode"
                  value: "937"
              }
          }
      """


#    #Data should be the Source IDs onboarded to monitoring
#
#  Scenario: All invalid requests
#    #Missing TimeZone and InputValueType
#    Given I build the following requests
#      | ID | Source System ID | Source ID                                           | Description                           | Input Value Type | Time Zone        | Holiday Calendar Code | Start of Day Time Offset | End of Day Time Offset | Rollover Time Offset |
#      | 1  | CREDIT           | CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll | CFU GrossBalanceAll in JPMCB New York | MONETARY_VALUE   |                  | NAU                   | 0                        | 0                      | 0                    |
#      | 2  | LIQUIDITY        | USD_FRB_BAL_BAL                                     | Federal Reserve Bank Balance          |                  | America/New_York | NAU                   | -3*60*60*1000            | -3*60*60*1000          | -3*60*60*1000        |
#    And with following additional attributes
#      | ID | Attribute Name  | Value |
#      | 1  | legalEntityCode | 317   |
#      | 1  | currency        | USD   |
#      | 2  | legalEntityCode | 317   |
#      | 2  | currency        | USD   |
#    And with following dimensions
#      | ID | Dimension Name |
#      | 1  | clientName     |
#    And I submit these requests to monitoring
#    Then Monitoring should give this output
#      | Status      | ERROR                                                                         |
#      | Description | Request Rejected due to 2 invalid definitions.                                |
#      | Data        | Rejected: CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll,USD_FRB_BAL_BAL |
#
#    ##UPDATE - REJECT ALL.
#  Scenario: Partial valid request
#    #Missing TimeZone and InputValueType
#    Given I build the following requests
#      | ID | Source System ID | Source ID                                           | Description                           | Input Value Type | Time Zone        | Holiday Calendar Code | Start of Day Time Offset | End of Day Time Offset | Rollover Time Offset |
#      | 1  | CREDIT           | CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll | CFU GrossBalanceAll in JPMCB New York | MONETARY_VALUE   |                  | NAU                   | 0                        | 0                      | 0                    |
#      | 2  | LIQUIDITY        | USD_FRB_BAL_BAL                                     | Federal Reserve Bank Balance          | MONETARY_VALUE   | America/New_York | NAU                   | -3*60*60*1000            | -3*60*60*1000          | -3*60*60*1000        |
#    And with following additional attributes
#      | ID | Attribute Name  | Value |
#      | 1  | legalEntityCode | 317   |
#      | 1  | currency        | USD   |
#      | 2  | legalEntityCode | 317   |
#      | 2  | currency        | USD   |
#    And with following dimensions
#      | ID | Dimension Name |
#      | 1  | clientName     |
#    And I submit these requests to monitoring
#    Then Monitoring should give this output
#      | Status      | PARTIAL ERROR                                                 |
#      | Description | 1 definition onboarded and 1 rejected                         |
#      | Data        | Rejected: CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll |
#
#  Scenario: Monitoring is unable to process requests due to some reason
#    Given I build the following requests
#      | ID | Source System ID | Source ID                                           | Description                           | Input Value Type | Time Zone        | Holiday Calendar Code | Start of Day Time Offset | End of Day Time Offset | Rollover Time Offset |
#      | 1  | CREDIT           | CRD.All.LE_317.CreditFamilyUsage-by-GrossBalanceAll | CFU GrossBalanceAll in JPMCB New York | MONETARY_VALUE   | America/New_York | NAU                   | 0                        | 0                      | 0                    |
#      | 2  | LIQUIDITY        | USD_FRB_BAL_BAL                                     | Federal Reserve Bank Balance          | MONETARY_VALUE   | America/New_York | NAU                   | -3*60*60*1000            | -3*60*60*1000          | -3*60*60*1000        |
#    And with following additional attributes
#      | ID | Attribute Name  | Value |
#      | 1  | legalEntityCode | 317   |
#      | 1  | currency        | USD   |
#      | 2  | legalEntityCode | 317   |
#      | 2  | currency        | USD   |
#    And with following dimensions
#      | ID | Dimension Name |
#      | 1  | clientName     |
#    And I submit these requests to monitoring
#    Then Monitoring should give this output
#      | Status      | ERROR                                                 |
#      | Description | Unable to respond to request, please try again later. |
#      | Data        | Rejected:                                             |
#
#  Scenario: Empty onboarding request
#    And I submit these requests to monitoring
#    Then Monitoring should give this output
#      | Status      | ERROR                   |
#      | Description | Empty request received. |
#      | Data        | Rejected:               |