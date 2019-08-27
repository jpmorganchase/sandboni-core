## The purpose of this acceptance test is to reproduce bugs detected in the system
#
#Feature: Generic projection model report generation
#
#  Scenario: Validate Generic projection model report generation
#
#    Given The time is 2018-08-13T00:00:00Z
#
#    And the Lightweight Liquidity Position data set in features/flows/gcf/gpm/gpm_lightweight_liquidity_positions.csv is loaded into the data grid
#
#    When we subscribe to a generic projection model report for currency GBP, legal entity 3305 and date 2018-08-13
#
#    Then the generated generic projection model report should have the following rows with values
#      | uri                                                          | label                         | sourceSystem | predicted | confirmed |
#      |                                                              | PROJECTION TOTALS             |              | 43000000  | 43000000  |
#      | startOfDayBalance                                            | Start of Day Balance          | SOD          |           |           |
#      | cio                                                          | CIO                           |              | 2000000   | 2000000   |
#      | cio/moneyMarket                                              | Money Market                  | OPICS        | 1000000   | 1000000   |
#      | cio/foreignExchange                                          | Foreign Exchange              | TSSFX        |           |           |
#      | cio/derivatives                                              | Derivatives                   | DCPP         |           |           |
#      | cio/securities                                               | Securities                    |              | 1000000   | 1000000   |
#      | cio/securities/fixedIncome                                   | Fixed Income                  | FIISCO       |           |           |
#      | cio/securities/investmentSecurities                          | Investment Securities         | OPICS        | 1000000   | 1000000   |
#      | cb                                                           | Commercial Bank               |              | 1000000   | 1000000   |
#      | cb/moneyMarket                                               | Money Market                  | OPICS        | 1000000   | 1000000   |
#      | cb/foreignExchange                                           | Foreign Exchange              | TSSFX        |           |           |
#      | cb/derivatives                                               | Derivatives                   | DCPP         |           |           |
#      | cb/fixedIncome                                               | Fixed Income                  | FIISCO       |           |           |
#      | cib                                                          | Corporate and Investment Bank |              | 20000000  | 20000000  |
#      | cib/moneyMarket                                              | Money Market                  | OPICS        | 1000000   | 1000000   |
#      | cib/collateral                                               | COLLATERAL                    | OPICS        | 1000000   | 1000000   |
#      | cib/foreignExchange                                          | Foreign Exchange              | TSSFX        |           |           |
#      | cib/derivatives                                              | Derivatives                   | DCPP         | 2000000   | 2000000   |
#      | cib/fixedIncome                                              | Fixed Income                  | FIISCO       |           |           |
#      | cib/globalRates                                              | Global Rates                  |              |           |           |
#      | cib/globalRates/globalRatesAndExotics                        | Global Rates and Exotics      |              |           |           |
#      | cib/globalRates/globalRatesAndExotics/foreignExchange        | Foreign Exchange              | TSSFX        |           |           |
#      | cib/globalRates/globalRatesAndExotics/derivatives            | Derivatives                   | DCPP         |           |           |
#      | cib/globalRates/currenciesAndEmergingMarkets                 | Currencies & Emerging Markets |              |           |           |
#      | cib/globalRates/currenciesAndEmergingMarkets/foreignExchange | Foreign Exchange              | TSSFX        |           |           |
#      | cib/globalRates/currenciesAndEmergingMarkets/derivatives     | Derivatives                   | DCPP         |           |           |
#      | cib/globalRates/globalFxExoticsAndHybrids                    | Global Fx Exotics & Hybrids   |              |           |           |
#      | cib/globalRates/globalFxExoticsAndHybrids/foreignExchange    | Foreign Exchange              | TSSFX        |           |           |
#      | cib/globalRates/globalFxExoticsAndHybrids/derivatives        | Derivatives                   | DCPP         |           |           |
#      | cib/primeBrokerage                                           | Prime Brokerage               |              |           |           |
#      | cib/primeBrokerage/foreignExchange                           | Foreign Exchange              | TSSFX        |           |           |
#      | cib/primeBrokerage/derivatives                               | Derivatives                   | DCPP         |           |           |
#      | cib/globalCommodities                                        | Global Commodities            |              |           |           |
#      | cib/globalCommodities/foreignExchange                        | Foreign Exchange              | TSSFX        |           |           |
#      | cib/globalCommodities/derivatives                            | Derivatives                   | DCPP         |           |           |
#      | cib/globalEquities                                           | Global Equities               |              | 16000000  | 16000000  |
#      | cib/globalEquities/equities                                  | Equities                      | NAPOLI       | 8000000   | 8000000   |
#      | cib/globalEquities/derivatives                               | Derivatives                   | DCPP         | 8000000   | 8000000   |
#      | loans                                                        | Loans                         |              |           |           |
#      | loans/loans                                                  | Loans                         | LoanIQ       |           |           |
#      | ccb                                                          | Consumer and Community Bank   |              | 3000000   | 3000000   |
#      | ccb/moneyMarket                                              | Money Market                  | OPICS        | 1000000   | 1000000   |
#      | ccb/foreignExchange                                          | Foreign Exchange              | TSSFX        |           |           |
#      | ccb/derivatives                                              | Derivatives                   | DCPP         | 2000000   | 2000000   |
#      | ccb/fixedIncome                                              | Fixed Income                  | FIISCO       |           |           |
#      | assetAndWealthManagement                                     | Asset & Wealth Management     |              | 8000000   | 8000000   |
#      | assetAndWealthManagement/wealthManagement                    | Wealth Management             | OLYMPIC      | 8000000   | 8000000   |
#      | otherLobActivity                                             | Other LoB Activity            |              | 9000000   | 9000000   |
#      | otherLobActivity/moneyMarket                                 | Money Market                  | OPICS        | 1000000   | 1000000   |
#      | otherLobActivity/foreignExchange                             | Foreign Exchange              | TSSFX        |           |           |
#      | otherLobActivity/derivatives                                 | Derivatives                   | DCPP         | 2000000   | 2000000   |
#      | otherLobActivity/fixedIncome                                 | Fixed Income                  | FIISCO       |           |           |
#      | otherLobActivity/equities                                    | Equities                      | NAPOLI       | 6000000   | 6000000   |
#      | other                                                        | Other                         |              |           |           |
#      | other/otherDrivers                                           | Other Drivers                 | MANUAL       |           |           |
#      | other/clients                                                | Clients                       | MANUAL       |           |           |