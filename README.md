# Brightline Loyalty - Points Service

Brightline Loyalty calculates reward points for a member's purchase in Ghana cedis (GHS). This is the refactored submission for the Code Quality & Testing applied lab.

## Rules

1. Earn one base point per full GHS 10.
2. Multiply whole base points by the member's tier multiplier and round down again.
3. Add 50 points when the purchase is strictly above GHS 500.

| Tier | Multiplier | Purchase (GHS) | Points |
|---|---:|---:|---:|
| STANDARD | 1.0 | 100 | 10 |
| GOLD | 2.0 | 250 | 50 |
| SILVER | 1.5 | 200 | 30 |
| STANDARD | 1.0 | 500 | 50 |
| STANDARD | 1.0 | 500.01 | 100 |
| GOLD | 2.0 | 600 | 170 |

Zero is valid. Negative and missing amounts are rejected. IDs must be nonblank and at most 128 characters. Lookups use the exact supplied ID. Missing members, technical lookup failures, unknown tiers and overflow are surfaced separately.

## Build and run

Use JDK 21 and Maven 3.9. The compiler release is 21. Verification used Maven 3.9.16 with BellSoft Liberica JDK 21.0.11 and Microsoft JDK 25.0.5.0.2. Run `mvn -v` to check which JDK Maven is using.

```shell
mvn clean verify
java -cp target/brightline-loyalty.jar com.brightline.loyalty.Demo
mvn test
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

In IntelliJ, select the project JDK for both the Maven runner and the Demo run configuration. The CLI prints successful awards to standard output and expected rejections to standard error. Unexpected failures propagate to the JVM.

## Structure and decisions

| Location | Responsibility |
|---|---|
| Demo | Wires the in-memory repository and default scheme, then demonstrates output. |
| model/Member | Immutable data record with an open-ended tier name. |
| repository | Member lookup contract, typed access failure and immutable in-memory sample data. |
| rule | Interfaces and implementations for tier and bonus rules. LoyaltyScheme validates and holds the registrations. |
| service | PointsCalculator validates and coordinates the calculation. Service exceptions communicate failures. |
| src/test | JUnit 5 cases and a Mockito repository failure fixture. |

Supply an additional `TierRule` to the scheme when adding a tier. Reuse `MultiplierTierRule` for a multiplier or implement `TierRule` for a different calculation. The calculator is unchanged by either extension. Default scheme settings live in code and require rebuilding to change.

## Verification

| Check | Result |
|---|---|
| Clean Maven run | 49 tests, 0 failures, 0 errors, 0 skipped |
| JaCoCo | 111/111 lines, 36/36 branches, 491/491 instructions |
| PIT | 35/35 mutants killed. No surviving or uncovered mutants. |

Both tools include all production classes. The demo test checks the exact output and error streams. During `mvn verify`, JaCoCo fails the build if any line, branch, or instruction is missed. PIT uses its default mutation operators and reports the actual score. Reports are generated at `target/site/jacoco/index.html` and `target/pit-reports/index.html`.

See the [architecture](ARCHITECTURE.md), [inherited code analysis](ANALYSIS.md), [design rationale](RATIONALE.md), [AI usage record](AI-USAGE.md), and [before](docs/before-refactor.txt) and [after](docs/after-refactor.txt) verification results.
