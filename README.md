# Closer Cards

An offline Android flashcard game for two people on a date. Share one phone, flip cards, and trade escalating questions across three sets of 12.

Inspired by research on structured self-disclosure and closeness (Aron et al., 1997). All questions in this app are original and are not the questions from the study.

## Features (MVP, v1.0)

- 36 original questions in 3 escalating sets of 12 (light, values, vulnerability)
- Tap-to-flip card with a turn banner that alternates who answers first
- Optional player names
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
3. First time only: the repo ships `gradle/wrapper/gradle-wrapper.properties` but not the wrapper scripts. Generate them from Studio's Gradle panel (**closer-cards > Tasks > build setup > wrapper**), then commit `gradlew`, `gradlew.bat` and `gradle/wrapper/gradle-wrapper.jar`.
4. Build a debug APK: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`)
5. Output: `app/build/outputs/apk/debug/app-debug.apk`

### Tests

- Unit tests (deck loading, turn logic): `./gradlew testDebugUnitTest`
- Full-session UI test (needs a running emulator or connected device): `./gradlew connectedDebugAndroidTest`

### Signed release APK for sideloading

Keystores and signing configs are never committed.

1. Create a keystore once, outside the repo or in its root (`*.jks` is gitignored):
   `keytool -genkeypair -v -keystore closer-cards.jks -keyalg RSA -keysize 2048 -validity 10000 -alias closercards`
2. Create `keystore.properties` in the repo root (gitignored):
   ```properties
   storeFile=closer-cards.jks
   storePassword=...
   keyAlias=closercards
   keyPassword=...
   ```
3. `./gradlew assembleRelease` produces `app/build/outputs/apk/release/app-release.apk`.
4. Install it with `adb install app/build/outputs/apk/release/app-release.apk`, or copy the APK to the phone and open it (allow "Install unknown apps" for your file manager when prompted).

Without `keystore.properties`, `assembleRelease` still builds, but the APK is unsigned and won't install.

## Project structure

```
closer-cards/
├── app/
│   └── src/
│       ├── main/
│       │   ├── assets/cards.json      # the 36-card deck
│       │   ├── java/com/tom/closercards/
│       │   │   ├── MainActivity.kt    # NavHost: home, setup, play
│       │   │   ├── SessionViewModel.kt
│       │   │   ├── model/             # Card + deck loader, session state machine
│       │   │   └── ui/                # Compose screens and theme
│       │   ├── res/values/strings.xml # all on-screen copy
│       │   └── AndroidManifest.xml    # no permissions at all
│       ├── test/                      # deck loading and turn-logic unit tests
│       └── androidTest/               # full-session Compose UI test
├── gradle/libs.versions.toml          # dependency versions
├── docs/
│   ├── closer-cards-spec-v1.0.docx    # product spec
│   └── claude-code-prompt-v1.0.md     # MVP implementation prompt
├── README.md
└── .gitignore
```

## Deck format

`app/src/main/assets/cards.json` is an array of cards. Edit it to change questions without touching code. The app expects exactly 3 sets with orders 1 to 12, and crashes on launch with a clear message if the deck doesn't match (`DeckTest` catches this first).

```json
[
  { "id": "s1-01", "set": 1, "order": 1, "text": "What is a small thing that reliably makes your day better?" }
]
```

## Roadmap

- P1: soft per-set timer, favorites, theme toggle and haptics, resume session, optional closing exercise
- P2: additional decks, share a card as an image, remote two-device mode

See `docs/closer-cards-spec-v1.0.docx` for the full prioritized feature table.

## Attribution

Concept inspired by: Aron, A., Melinat, E., Aron, E. N., Vallone, R. D., & Bator, R. J. (1997). The experimental generation of interpersonal closeness. *Personality and Social Psychology Bulletin*. Citation details to be verified before any public release.

## License

Personal project, not currently open for redistribution. Choose a license (for example MIT) before making the repo public.
