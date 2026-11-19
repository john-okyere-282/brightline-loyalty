# Inherited code review

The lab brief identifies the starter at fa5ba88 as AI-generated. At the initial check-in, main was at b4cedd6, which already injected the repository. The working tree also contained staged and unstaged package moves. The supplied lab brief and AmaliTech Coding Standards Java Edition draft (October 2026) informed the review. Sections 3.4, 4, 6, 8, 9, 11.1 and 12 informed the changes.

| Priority | Location | Problem and impact | Principle and proposed fix | Final status |
|---|---|---|---|---|
| Critical | PointsCalculator.calculatePoints | Broad catch turns failed lookups and calculation errors into a valid-looking zero. A caller can mark a purchase processed without awarding earned points. | Separate absent members and technical failures, preserving causes. | Fixed |
| High | PointsCalculator.calculatePoints | Adding a tier requires editing the calculator. Unknown tiers silently get base points. | Open/Closed Principle. Inject tier rules and reject unknown tiers. | Fixed |
| High | PointsCalculator.calculatePoints | Lookup, calculation, error handling and printing share one method and inputs are unvalidated. | Single Responsibility Principle. Separate rule configuration and output from calculation, validate before lookup. | Fixed |
| High | PointsCalculator.calculatePoints | double and int casts obscure rounding. Gold multiplication and bonus addition can overflow. The starter's double can represent NaN, which casts to zero. BigDecimal cannot represent NaN. | Use exact decimal inputs, explicit rounding and checked integer arithmetic. | Fixed |
| Medium | MemberRepository.findById | Null conflates missing data and a broken adapter. | Specify an Optional contract and a typed repository failure. | Fixed |
| Medium | Member.tier and PointsCalculator.calculatePoints | A comment implies a closed tier list and policy numbers are scattered. | Keep open tier names and collect named settings in a scheme that rejects duplicates. | Fixed |
| High | Starter PointsCalculator.repository | Hard-wired repository made lookup failures difficult to isolate. | Dependency Inversion Principle. Inject the existing narrow interface. | Fixed in b4cedd6 |

The one-method repository interface follows Interface Segregation. Implementations share the same documented return and exception contract, so the calculator can use a different adapter without changing how it handles absence or an outage.

## Inherited tests

| Priority | Test | Finding | Fix | Final status |
|---|---|---|---|---|
| High | calculatesGoldPoints | Verifies a stub on the calculator itself. No production calculation runs. | Assert 50 using the real calculator. | Fixed |
| High | awardsPointsForAPurchase | assertNotNull on a boxed int accepts every possible result. | Assert exactly 30. | Fixed |
| High | awardsPointsToAMember | The assertion accepts every positive result. | Assert exactly 10. | Fixed |
| High | PointsCalculatorTest | Missing boundaries, failure paths and overflow. | Add exact output and exception assertions with Mockito at the repository seam. | Fixed |

The highest priority is the hidden failure because zero is a valid answer. The initial formula divides by ten and truncates before multiplying, truncates again after the multiplier, and adds 50 strictly above GHS 500. These formulas match the supplied reference cases. See docs/before-refactor.txt for the observed baseline.
