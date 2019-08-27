Feature: Receive and process Cash Forecast messages of type cash management

  Scenario: Cash Forecast should receive and process Cash Forecasts of type cash management
    Given The time is 2017-09-19T00:00:00.00Z
    And The following CashForecast messages are stored in CashODS
      | messageId | messageSourceSystem | originatingSourceSystem | id | version | closeOfBusinessDate | valueDate  | type            | state            | settlementType | lineOfBusinessDomain | lineOfBusinessId | slsProductId | bookingLegalEntityIdDomain | bookingLegalEntityId | currency | amount | createdBy |bookingBusinessUnitCode|
      | 1         | IDL                 | Test                    | 1  | 1       | 2017-09-19          | 2017-09-19 | CASH_MANAGEMENT | CHECKER_APPROVED | NOP            | 1                    | 1                | 1            | 1                          | 1                    | USD      | 1000   | USER      |null                   |

    When Cash Forecast App receives the aaa following lightweight notifications
      | SID | Type            | TimeStamp               |
      | 1   | CASH_MANAGEMENT | 2017-09-19T00:00:00.00Z |

    Then no CashForecast messages should be published


  Scenario: Cash Forecast should receive and generate offsets for updates to a previously submitted Cash Forecast of type cash management
    Given The time is 2017-10-02T00:00:00.00Z

    And The following CashForecast messages are stored in CashODS
      | messageId | messageSourceSystem | originatingSourceSystem | id | version | closeOfBusinessDate | valueDate  | type            | state            | settlementType | lineOfBusinessDomain | lineOfBusinessId | slsProductId | bookingLegalEntityIdDomain | bookingLegalEntityId | currency | amount | createdBy |bookingBusinessUnitCode|
      | 1         | IDL                 | Test                    | 1  | 1       | 2017-10-02          | 2017-10-04 | CASH_MANAGEMENT | SYSTEM_APPROVED  | NOP            | 1                    | 1                | 1            | 1                          | 1                    | USD      | 1000   | USER      |null                    |
      | 2         | IDL                 | Test                    | 1  | 2       | 2017-10-02          | 2017-10-04 | CASH_MANAGEMENT | SYSTEM_APPROVED  | NOP            | 1                    | 1                | 1            | 1                          | 1                    | USD      | 2000   | USER      |null                    |

    When Cash Forecast App receives the following lightweight notifications
      | SID | Type            | TimeStamp               |
      | 2   | CASH_MANAGEMENT | 2017-10-02T00:00:00.00Z |

    Then the following CashForecast messages should be persisted in CASH_FORECAST_EVENT
      | messageId | messageSourceSystem | originatingSourceSystem | id | version | closeOfBusinessDate | valueDate  | type            | state           | settlementType | lineOfBusinessDomain | lineOfBusinessId | slsProductId | bookingLegalEntityIdDomain | bookingLegalEntityId | currency | amount     | createdBy | offsetIndicator | cashForecastSid | sourceSupplyType |bookingBusinessUnitCode|
      | 2         | IDL                 | Test                    | 1  | 2       | 2017-10-02          | 2017-10-04 | CASH_MANAGEMENT | SYSTEM_APPROVED | NOP            | 1                    | 1                | 1            | 1                          | 1                    | USD      | 2000.0000  | USER      | N               | 2               |                  |null                   |
      | 1         | IDL                 | Test                    | 1  | 2       | 2017-10-02          | 2017-10-04 | CASH_MANAGEMENT | SYSTEM_APPROVED | NOP            | 1                    | 1                | 1            | 1                          | 1                    | USD      | -1000.0000 | SYSTEM    | Y               | 1               |                  |null                   |