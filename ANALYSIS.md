# Review of the Inherited Brightline Loyalty Code

## 1. Baseline Test Results

Before making any changes, I ran `mvn test` on the original starter project to understand its current state. The test output was saved in `before-refactor.txt`.

The initial test run completed successfully:

- 3 tests executed.
- 0 failures.
- 0 errors.
- 0 skipped tests.
- Build status: `BUILD SUCCESS`.

The application also printed the following messages:

```text
Awarded 10 points to Kofi Mensah
Awarded 30 points to Yaa Boateng
```

These messages came from the `System.out.println` statement in problem 6. The mocked gold-tier test produced no output because it did not execute the actual application logic (T1).

Although all tests passed, this did not necessarily mean the implementation was correct. A closer review revealed several design, validation, and testing problems that were not covered by the existing test suite.

## 2. Most Critical Issue

**Exception handling in `PointsCalculator`:**

```java
catch (Exception e) {
    return 0;
}
```

This was the most concerning issue in the original implementation.

When a member cannot be found, `repository.findById()` returns `null`. The application then attempts to access the member's information, resulting in a `NullPointerException`.

However, the catch block intercepts the exception and returns `0` instead.

This creates a problem because different situations are treated as though they have the same outcome:

- A member does not exist.
- An error occurs during the calculation.
- An invalid or unknown membership tier is provided.

All these situations can result in zero points without explaining what went wrong.

As a result, the application may display an incorrect result without notifying the caller that something failed.

I considered this the highest-priority issue because the system is responsible for calculating customer loyalty rewards. Returning an incorrect result while hiding the underlying error could lead to incorrect reward records.

It also exposes a weakness in the existing tests because they do not identify this behaviour.

This does not follow the exception-handling expectations in sections 6.1 and 6.3 of the coding guidelines.

## 3. Issues Identified in the Original Implementation

The following table summarises the main problems identified during the review and the improvements planned for each one.

| # | Issue | Location | Why It Is a Problem | Planned Improvement |
|---|---|---|---|---|
| 1 | Exceptions are silently handled | `PointsCalculator` lines 32–35 | Errors are converted into normal results, making failures difficult to identify. | Introduce `MemberNotFoundException` and `InvalidPurchaseException`. Remove the general catch block. |
| 2 | Dependency is hard-coded | `PointsCalculator` line 7 | The repository cannot easily be replaced, making testing and future changes difficult. | Inject `MemberRepository` through the constructor. |
| 3 | Tier logic uses an if/else chain | `PointsCalculator` lines 17–23 | Adding another membership tier requires modifying existing calculation logic. | Introduce a `TierRule` interface with separate implementations for each tier and a registry to connect tiers to their rules. |
| 4 | Too many responsibilities in one method | `PointsCalculator` lines 9–36 | Member lookup, calculations, bonuses, printing, and error handling are combined in one method. | Separate the calculation into smaller responsibilities. |
| 5 | Magic numbers are used | Values such as `10`, `1.5`, `2`, `500`, and `50` | The purpose of these values is not immediately clear, and changing business rules becomes harder. | Replace them with clearly named constants and keep tier multipliers within their respective rules. |
| 6 | Printing is included in calculation logic | `PointsCalculator` line 30 | Printing introduces an unnecessary side effect and makes the calculation harder to test. | Remove printing from the calculation. Logging can be handled by the calling layer if required. |
| 7 | Repository returns `null` | `InMemoryMemberRepository` line 18 | Callers must remember to check for null values, which can lead to runtime exceptions. | Return `Optional<Member>` to make the possibility of a missing member explicit. |
| 8 | `double` is used for monetary values | `PointsCalculator` line 14 | Floating-point calculations can introduce precision problems, while casting to `int` can silently truncate values. | Use `BigDecimal`, validate inputs, and apply an explicit rounding strategy. |
| 9 | Membership tier is represented as a String | `Member` line 6 | Invalid tier names and spelling mistakes can be introduced without compiler warnings. | Introduce a `Tier` enum and represent `Member` as a record. |
| 10 | Some comments repeat what the code already explains | `PointsCalculator` lines 5–6, 13, 16, 25, and 33 | Unnecessary comments make the implementation harder to read and maintain. | Remove redundant comments and retain only useful explanations. |
| 11 | Minor coding style issues | `Demo`, `Member`, and test classes | Some formatting and import choices do not follow the expected coding standards. | Clean up the comments, getters, and wildcard imports. |

## 4. Problems Found in the Existing Tests

Although the original test suite passed, reviewing the tests showed that several assertions did not properly verify the application's behaviour.

A successful test result is only useful when the tests actually execute the code and check meaningful expected outcomes.

The following issues were identified:

| # | Test | Problem Identified | Planned Improvement |
|---|---|---|---|
| T1 | `calculatesGoldPoints` | The test mocks `PointsCalculator` itself and then checks a value that was already configured in the mock. The actual calculation is never executed. | Remove this test and test the real calculator using a mocked `MemberRepository`. Verify that a GOLD member purchasing 250 earns 50 points. |
| T2 | `awardsPointsForAPurchase` | Uses `assertNotNull` on an integer result. Since an integer cannot be null, the assertion does not provide meaningful verification. | Assert the exact expected result: a SILVER member purchasing 200 earns 30 points. |
| T3 | `awardsPointsToAMember` | Only checks whether the result is greater than zero. This does not verify whether the calculation is correct. | Assert the exact expected result: a STANDARD member purchasing 100 earns 10 points. |
| T4 | Duplicate test coverage | T2 and T3 follow almost the same testing approach, with different member IDs. | Use a parameterized test to verify different tiers and purchase amounts against their expected results. |
| T5 | Missing boundary tests | The existing tests do not cover the 500 bonus threshold. Changes to the bonus condition could go unnoticed. | Add tests for values below, at, and above the threshold. |
| T6 | Repository cannot be mocked | The hard-coded repository dependency makes it difficult to isolate the calculator during testing. | Inject `MemberRepository` and use Mockito to test both successful and failed repository operations. |
| T7 | Wildcard imports | The test classes use wildcard imports, which reduce clarity about the dependencies being used. | Replace wildcard imports with explicit imports. |

## 5. Additional Test Coverage Required

To make the application more reliable, the improved test suite should cover normal operations, boundary conditions, invalid inputs, and failure scenarios.

The following cases were identified as important:

### 5.1 Normal Calculation Scenarios

Verify that each membership tier calculates points correctly using typical purchase amounts.

- STANDARD tier.
- SILVER tier.
- GOLD tier.

### 5.2 Boundary Conditions

Test values around the base-points and bonus thresholds:

- `9.99` – should produce no base points.
- `10` – should produce the first base point.
- `500` – should not receive the bonus.
- `500.01` – should qualify for the bonus.

### 5.3 Rounding Behaviour

Verify that calculations involving multipliers produce the expected results.

For example:

- A SILVER member with 10 base points should receive 15 points.

### 5.4 Error Handling

The test suite should also verify how the application responds to invalid situations:

- A member who does not exist.
- A negative purchase amount.
- A `NaN` purchase amount.
- A repository failure.

These tests should confirm that errors are handled appropriately instead of being silently converted into zero points.

### 5.5 Extensibility

The design should also be tested to confirm that a new membership tier can be introduced without modifying the existing tier calculation implementations.

This will help verify that the implementation supports the Open/Closed Principle.

## 6. Conclusion

The initial review showed that the Brightline Loyalty starter project had several issues that were not visible from its successful test results.

The main concerns were weak exception handling, tightly coupled dependencies, duplicated responsibilities, inflexible tier calculations, unclear business values, and insufficient test coverage.

The improvements identified during this review provide a direction for restructuring the application around better separation of responsibilities, dependency injection, stronger validation, and more meaningful automated tests.

The objective is not simply to make the existing tests pass. It is to build an implementation that is easier to understand, test, extend, and maintain while preserving the intended loyalty calculation rules.