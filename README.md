# Closer Cards

An offline Android flashcard game for two people on a date. Share one phone, flip cards, and trade escalating questions across three sets of 12.

Inspired by research on structured self-disclosure and closeness (Aron et al., 1997). All questions in this app are original and are not the questions from the study.

## Features (MVP, v1.0)

- 36 original questions in 3 escalating sets of 12 (light, values, vulnerability)
- Tap-to-flip card with a turn banner that alternates who answers first
- Optional player names
- Optional timer for each question (off, 1.5, 3 or 5 minutes) that shades in Next as a pacing cue; Next works at any time
- Skip on every card; Set 3 reminds players that either person may pass
- Set intro and set-complete screens, progress indicator, session-complete screen
- Fully offline: no INTERNET permission, no accounts, no analytics, no ads

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Single activity, Navigation Compose
- MVVM with ViewModel and StateFlow (in-memory session state)
- kotlinx.serialization for the bundled deck
- minSdk 26

## Getting started

1. Clone the repo and open it in Android Studio (latest stable).
2. Let Gradle sync, then run the `app` configuration on a device or emulator.
3. Build a debug APK: `./gradlew assembleDebug`
4. Output: `app/build/outputs/apk/debug/app-debug.apk`

From the command line, Gradle needs JDK 17 or newer (Android Studio's bundled JBR works) and the SDK location in `ANDROID_HOME` or `local.properties`.

Checks:

- `./gradlew testDebugUnitTest`: deck parsing and session logic (JVM, no device)
- `./gradlew connectedDebugAndroidTest`: plays a full session through the UI on a connected device or emulator
- `./gradlew lintDebug`: expected to report 0 errors and 0 warnings

### Release build

Release builds are shrunk with R8 and signed locally for sideloading. Keystores and `keystore.properties` are git-ignored and never committed.

1. Create a keystore once, in the repo root (keytool asks for a password; keep the file and password backed up, since updates must be signed with the same key):
   `keytool -genkeypair -v -keystore closer-cards.jks -alias closer-cards -keyalg RSA -keysize 2048 -validity 10000`
2. Create `keystore.properties` in the repo root:
   ```
   storeFile=closer-cards.jks
   storePassword=<your password>
   keyAlias=closer-cards
   keyPassword=<your password>
   ```
3. `./gradlew assembleRelease`
4. Output: `app/build/outputs/apk/release/app-release.apk`. Without `keystore.properties` the build still works but produces `app-release-unsigned.apk`, which will not install.

### Google Play

Play takes an app bundle, not an APK: `./gradlew bundleRelease` writes `app/build/outputs/bundle/release/app-release.aab`, signed with the same keystore (it becomes the Play upload key). Raise `versionCode` in `app/build.gradle.kts` for every upload. Listing text, the privacy policy and the store graphics are in `docs/play-store/`.

## Project structure

```
closer-cards/
├── app/
│   └── src/
│       ├── main/
│       │   ├── assets/cards.json      # the deck: sets and their cards
│       │   ├── java/com/tom/closercards/
│       │   │   ├── MainActivity.kt    # theme + CloserCardsNavHost
│       │   │   ├── model/             # Deck + DeckRepository (cards.json), Session, SessionReducer, TurnOrder
│       │   │   └── ui/                # CloserCardsViewModel, CloserCardsNavHost, screens/, theme/, components/
│       │   ├── res/                   # strings, day/night window themes, launcher icon, fonts, backup rules
│       │   └── AndroidManifest.xml    # no permissions at all, INTERNET included
│       ├── test/                      # deck and session unit tests
│       └── androidTest/               # full-session Compose UI test
├── docs/
│   ├── closer-cards-spec-v1.0.docx    # product spec
│   ├── claude-code-prompt-v1.0.md     # MVP implementation prompt
│   └── design-system.md               # theme, components, fonts and how to use them
├── README.md
└── .gitignore
```

## How a session runs

Home, Setup (optional names; blank ones become "Player A" and "Player B"; a timer choice that is not saved between launches), then for each set: Set intro, its cards, and a Set complete pause with Continue or End session. After the last set comes Session complete, whose Play again starts set 1 again with the same names.

`CloserCardsViewModel` holds the session in memory and exposes it as a `StateFlow`; every transition is a pure function in `model/Session.kt` (`SessionReducer`), so the rules are unit-tested without Compose. Next and Skip move the same way (a skipped card counts as done). Turn order restarts in every set: Player A answers first on odd cards, Player B on even ones. System Back from any screen ends the session and returns Home.

## Deck format

`app/src/main/assets/cards.json` holds the sets and the cards. Edit it to change questions, set copy, the number of cards in a set or the number of sets, without touching code. The app checks the file at launch and stops with a message naming the problem if it is malformed.

```json
{
  "sets": [
    { "set": 1, "title": "Light and personal history", "guidance": "Keep it light; there are no wrong answers." },
    { "set": 3, "title": "Vulnerability and connection", "guidance": "Go as deep as feels right tonight.",
      "passNote": "Either of you may pass on any question." }
  ],
  "cards": [
    { "id": "s1-01", "set": 1, "order": 1, "text": "What is a small thing that reliably makes your day better?" }
  ]
}
```

Rules: sets are numbered 1 to N; `passNote` is optional and shown on that set's intro; every card has a unique `id`, belongs to a listed set, has an `order` unique within its set, and has text. Cards play in `order` within each set. Only three set colours exist, so a fourth set would reuse set 1's colour.

## Roadmap

- P1: soft per-set timer, favorites, theme toggle and haptics, resume session, optional closing exercise
- P2: additional decks, share a card as an image, remote two-device mode

See `docs/closer-cards-spec-v1.0.docx` for the full prioritized feature table.

## Attribution

Concept inspired by: Aron, A., Melinat, E., Aron, E. N., Vallone, R. D., & Bator, R. J. (1997). The experimental generation of interpersonal closeness. *Personality and Social Psychology Bulletin*. Citation details to be verified before any public release.

## License

Personal project, not currently open for redistribution. Choose a license (for example MIT) before making the repo public.
