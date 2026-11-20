# Refactoring rationale

## Preserve the reward formula

The starter calculated whole base points before multiplying. Keeping that order matters. A SILVER purchase of GHS 19.99 earns one base point, then 1.5 tier points rounded down to one. Multiplying before the first rounding would yield two. BigDecimal stores decimal amounts exactly and compareTo keeps the bonus boundary independent of the number of decimal places.

## Give each dependency a role

The existing injected MemberRepository stays. Optional.empty expresses a normal absence. RepositoryAccessException lets adapters report an operational problem with its original cause. The calculator catches that declared failure and translates it into MemberLookupException. A broken null Optional is reported explicitly and unexpected programming failures propagate. The console demo handles expected business rejections at the entry point.

The custom exceptions live in one `exception` package so their definitions are easy to find. The repository still declares its checked access failure, and the calculator still decides how each failure reaches its caller.

Member is a record because it carries three immutable strings. Tier names remain open strings so registration can introduce names without editing a shared enum. LoyaltyScheme owns duplicate checks and defensive collection copies. The built-in rules are immutable. Custom rules should honor the interface contracts and remain stable after registration.

A switch would be short for the current tiers, but every new tier would require editing the calculator. TierRule and BonusRule each describe one kind of calculation, and the caller chooses the rules. One multiplier implementation covers all three current tiers. Separate classes for each default tier would repeat the same calculation. Moving policy values to external configuration made little sense for a small standalone lab without a requirement to edit policy at runtime.

## Improvement beyond the brief

The calculation rejects overflow at every integer boundary. Exact conversion protects both base and tier points. Checked addition protects both bonus accumulation and the final total. A dedicated error preserves the arithmetic cause. A large purchase or a new bonus can no longer silently corrupt a result. Tests include the largest representable STANDARD total and separate failures at each calculation step.

## What changed in the inherited tests

The STANDARD, SILVER, and GOLD examples remain in a parameterized test that executes the real calculator with the in-memory adapter. Exact results replace the positive-only check, the boxed-int null check, and the self-mock. Mockito supplies a repository outage so the test can verify cause preservation through the real calculator. Boundary and failure tests assert the relevant result or error detail. PIT showed that the suite originally missed the inclusive ID length limit. A test at exactly 128 characters now detects that change.

## Standards and scope

The supplied AmaliTech Coding Standards Java Edition draft guided named constants (3.4), small methods (4), preserving causes (6), data records (8), behavioral assertions (9), Java formatting (11.1), and SOLID (12). The inherited com.brightline.loyalty namespace is retained. The coverage gate includes every production class. The demo test verifies exact awards and rejections at the console boundary. See the recorded results for the verified runtimes and build diagnostics.
