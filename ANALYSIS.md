# Inherited code review

The README identifies the starter at fa5ba88 as AI-generated. Main is at b4cedd6, which already injects the repository. The working tree includes staged and unstaged package moves. The supplied lab brief guides the review. Separate AmaliTech guidelines were not available in the repository.

| Priority | Location | Problem and impact | Principle and proposed fix | Check-in status |
|---|---|---|---|---|
| Critical | PointsCalculator.calculatePoints | Broad catch turns failed lookups and calculation errors into a valid-looking zero. A caller can mark a purchase processed without awarding earned points. | Separate absent members and technical failures, preserving causes. | Open |
| High | PointsCalculator.calculatePoints | Adding a tier requires editing the calculator. Unknown tiers silently get base points. | Open/Closed Principle. Inject tier rules and reject unknown tiers. | Open |
| High | PointsCalculator.calculatePoints | Lookup, calculation, error handling and printing share one method and inputs are unvalidated. | Single Responsibility Principle. Separate rule configuration and output from calculation, validate before lookup. | Open |
| High | PointsCalculator.calculatePoints | double and int casts obscure rounding. Gold multiplication and bonus addition can overflow. The starter's double can represent NaN, which casts to zero. BigDecimal cannot represent NaN. | Use exact decimal inputs, explicit rounding and checked integer arithmetic. | Open |
| Medium | MemberRepository.findById | Null conflates missing data and a broken adapter. | Specify an Optional contract and a typed repository failure. | Open |
| Medium | Member.tier and PointsCalculator.calculatePoints | A comment implies a closed tier list and policy numbers are scattered. | Keep open tier names and collect named settings in a scheme that rejects duplicates. | Open |
| High | Starter PointsCalculator.repository | Hard-wired repository made lookup failures difficult to isolate. | Dependency Inversion Principle. Inject the existing narrow interface. | Fixed in b4cedd6 |

The one-method repository interface has no clear Interface Segregation violation. Its documented return and exception contract will ensure implementations are substitutable.

## Inherited tests

| Priority | Test | Finding | Fix | Check-in status |
|---|---|---|---|---|
| High | calculatesGoldPoints | Verifies a stub on the calculator itself. No production calculation runs. | Assert 50 using the real calculator. | Open |
| High | awardsPointsForAPurchase | assertNotNull on a boxed int accepts every possible result. | Assert exactly 30. | Open |
| High | awardsPointsToAMember | The assertion accepts every positive result. | Assert exactly 10. | Open |
| High | PointsCalculatorTest | Missing boundaries, failure paths and overflow. | Add exact output and exception assertions with Mockito at the repository seam. | Open |

The highest priority is the hidden failure because zero is a valid answer. The initial formula divides by ten and truncates before multiplying, truncates again after the multiplier, and adds 50 strictly above GHS 500. These formulas match the supplied reference cases. See docs/before-refactor.txt for the observed baseline.
