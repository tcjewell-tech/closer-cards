# Claude Code Prompt: Closer Cards (MVP, v1.0)

Companion spec: `closer-cards-spec-v1.0` (Drive folder: Closer Cards). Scope below is P0 only. Do not build P1 or P2 items.

## Goal
Build an Android-only, offline flashcard game for two people on a date. They share one phone, take turns flipping cards, and answer escalating questions across 3 sets of 12.

## Stack
- Kotlin, Jetpack Compose, Material 3, single activity, Navigation Compose
- MVVM with ViewModel + StateFlow; in-memory session state only
- kotlinx.serialization to parse `app/src/main/assets/cards.json`
- minSdk 26, target latest stable SDK
- No INTERNET permission, no analytics, no ads, no accounts, no third-party SDKs
- Package: `com.tom.closercards` (change if preferred)

## Screens and flow
1. Home: title, Start button, one-line "how it works".
2. Setup: optional names for Player A and Player B (default "Player A" / "Player B").
3. Set intro: set number, theme, one line of guidance.
4. Card: face-down card showing "Set X, Card Y of 12". Tap flips it (graphicsLayer rotationY, camera distance set) to reveal the question. Turn banner shows who answers first: Player A on odd cards, Player B on even cards. Both answer. Buttons: Next, Skip (always visible).
5. Set complete: pause screen with Continue and End session.
6. Session complete: closing message and Play Again.

Set 3 intro must note that either person may pass on any question.

## Data
`cards.json` is an array of `{ "id": "s1-01", "set": 1, "order": 1, "text": "..." }`. Load the 36 cards below, ordered by set then order. Deck must be easy to edit without code changes.

## Draft content (original; owner to review before release)
Set 1 (light, personal history)
1. What is a small thing that reliably makes your day better?
2. What was your favorite place to spend time as a kid?
3. If you could have dinner with anyone from any era, who would it be and why?
4. What is a skill you wish you had picked up years ago?
5. What does a perfect lazy Sunday look like for you?
6. What is the best trip you have ever taken, and what made it great?
7. Which song or album takes you straight back to a specific time in your life?
8. What is a harmless habit or quirk that friends tease you about?
9. What did you want to be when you grew up, and how close did you get?
10. What is the best meal you have ever eaten, and who were you with?
11. What is something you are unreasonably good at?
12. What is something you are looking forward to in the next few months?

Set 2 (values and experiences)
13. Who has influenced the way you see the world most, and how?
14. What do you value most in a friendship?
15. What was a turning point that changed the direction of your life?
16. How did your family shape the way you handle conflict?
17. What does a good day at work or in your daily routine give you?
18. When you have had a rough week, what helps you reset?
19. What is something you have changed your mind about in the last few years?
20. How do you like to show people you care about them?
21. What is a risk you took that you are glad you did?
22. What does home mean to you?
23. What is a tradition from your life that you hope to keep?
24. What is one thing you wish people understood about you sooner?

Set 3 (vulnerability and connection)
25. What is something you are still learning to forgive yourself for?
26. When do you feel most like yourself?
27. What is a fear you do not often say out loud?
28. What do you need most from a partner when things get hard?
29. What is a moment you wish you could do over, and what would you change?
30. What has been the hardest thing to let go of?
31. What does being truly known by someone look like to you?
32. What is something you are proud of that few people know about?
33. How do you want to be remembered by the people closest to you?
34. What is a pattern in past relationships that you want to do differently?
35. What are you hoping for in the next chapter of your life?
36. What is something you have enjoyed learning about me tonight?

## Requirements
- Fully offline; verify `AndroidManifest.xml` declares no INTERNET permission.
- Accessibility: 48dp minimum touch targets, content descriptions, text scales with system font, adequate contrast.
- Performance: 60 fps flip on mid-range device, cold start under 2 seconds.
- Dark and light theme follow system (colors only; no theme toggle in MVP).

## Tests
- Unit: deck loads 36 cards, 12 per set, ordered correctly; turn logic alternates A/B by card order; skip advances without breaking progress.
- One Compose UI test covering a full session from Home to Session complete.

## Deliverables
- Android Studio project that builds a debug APK and a signed release APK for sideloading
- README with build and install steps
- Do not add P1/P2 features (timer, favorites, themes toggle, resume, extra decks, sharing, remote play)

## Open items to confirm before release
- Final app name (working title: Closer Cards)
- Owner approval of the 36 draft questions, especially Set 3 tone
- minSdk 26 vs the owner's device
