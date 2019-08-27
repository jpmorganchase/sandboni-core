#RSPPORTAL-862 UI - Bookmarks List Display Grid - QA Sub-task 1294

@RSP-2345 @RSP-2344

@RSP-2346
Feature: Bookmarks List Display Grid
  Description:
  * As a FA/RA, Wholesaler,
  * I want to be able to see a list of the calendar deals that I am interested
  * in (bookmarked) with supporting reference data and basic table functions within
  * the table (sorting, filtering) so that I can see and
  * review the products in a single location for reference when speaking to clients

  @RSPPORTAL-862 @TC1 @TC24 @TC8
  Scenario: Displays bookmarks and default actions in Bookmark page
    Given  I am logged into RSP Portal as an American Advisor
    When   I click the Bookmarks tab
    Then   The Bookmarks Table is displayed with correct column headers
    And    The calendar deals are sorted by bookmarked dates most recently added first
    And    The \Date Added\ column header has arrow pointing down to indicate sort direction

  @RSPPORTAL-862
  Scenario: Verify Date Added Sort works in Bookmark Page
    When I click on Date Added column in bookmark page once
    Then The Date Added column in bookmark page should be sorted from smallest to largest date
    And  The arrow should be pointing up in Date Added field
    When I click on Date Added column in bookmark second time
    Then The Date Added column in bookmark page should be sorted from  largest to smaller date
    And  The arrow should be pointing down in Date Added field