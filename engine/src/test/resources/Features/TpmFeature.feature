Feature: Receive and process Cash Forecast messages of type cash management

  Scenario: Cash Forecast should receive and process Cash Forecasts of type cash management
    Given The time is 2017-09-19T00:00:00.00Z
    And The following CashForecast messages are stored
      | messageId | messageSourceSystem | originatingSourceSystem | id | version | closeOfBusinessDate | valueDate  | type            |
      | 1         | AAA                 | Test                    | 1  | 1       | 2017-09-19          | 2017-09-19 | CASH_MANAGEMENT |

    When Cash Forecast App receives the aaa following lightweight notifications
      | SID | Type            | TimeStamp               |
      | 1   | CASH_MANAGEMENT | 2017-09-19T00:00:00.00Z |

    Then no CashForecast messages should be published


  Scenario: Cash Forecast should receive and generate offsets for updates to a previously submitted Cash Forecast of type cash management
    Given The time is 2017-10-02T00:00:00.00Z

    And The following CashForecast messages are stored
      | messageId | messageSourceSystem | originatingSourceSystem | id | version | closeOfBusinessDate | valueDate  | type            |
      | 1         | AAA                 | Test                    | 1  | 1       | 2017-10-02          | 2017-10-04 | CASH_MANAGEMENT |
      | 2         | AAA                 | Test                    | 1  | 2       | 2017-10-02          | 2017-10-04 | CASH_MANAGEMENT |

    When Cash Forecast App receives the following lightweight notifications
      | SID | Type            | TimeStamp               |
      | 2   | CASH_MANAGEMENT | 2017-10-02T00:00:00.00Z |

    Then the following CashForecast messages should be persisted in CASH_FORECAST
      | messageId | messageSourceSystem | originatingSourceSystem | id | version | closeOfBusinessDate | valueDate  | type            |
      | 2         | AAA                 | Test                    | 1  | 2       | 2017-10-02          | 2017-10-04 | CASH_MANAGEMENT |
      | 1         | AAA                 | Test                    | 1  | 2       | 2017-10-02          | 2017-10-04 | CASH_MANAGEMENT |