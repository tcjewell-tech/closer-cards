# Closer Cards

An offline Android flashcard game for two people on a date. Share one phone, flip cards, and trade escalating questions across three sets of 12.

Inspired by research on structured self-disclosure and closeness (Aron et al., 1997). All questions in this app are original and are not the questions from the study.

## Features (MVP, v1.0)

- 36 original questions in 3 escalating sets of 12 (light, values, vulnerability)
- Two optional adult decks, **After Dark** (explicit) and **After Darker** (kink/BDSM), each behind a consent screen and with its own dark palette
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
- minSdk 26, target/compile SDK 37; AGP 9.4, Gradle 9.8, Kotlin 2.4

## Getting started

1. Clone the repo and open it in Android Studio (latest stable).
2. Let Gradle sync, then run the `app` configuration on a device or emulator.
3. Build a debug APK: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`). From a plain terminal, point `JAVA_HOME` at Studio's bundled JDK first (`C:\Program Files\Android\Android Studio\jbr`).
4. Output: `app/build/outputs/apk/debug/app-debug.apk`

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
│       │   ├── assets/decks.json      # deck list: titles, set themes, guidance, consent text
│       │   ├── assets/cards*.json     # one 36-card file per deck
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

## Web version

Play in a browser and edit the questions without a phone:

```
node web/server.mjs
```

Open http://127.0.0.1:8787. The **Edit questions** links on the home screen save straight to the deck files in `app/src/main/assets/`, the same files the app uses (rebuild the app to see changes on the phone). The server only listens on this computer, rejects decks that aren't 3 sets of 12, and needs nothing beyond Node. Tests: `node --test web/server.test.mjs`.

## Deck format

`app/src/main/assets/decks.json` lists the decks in home-screen order (the first is the main game). Each entry names its cards file, three set themes, three guidance lines and, for adult decks, the consent text. Each cards file is an array of cards; edit it to change questions without touching code. Every deck must be exactly 3 sets with orders 1 to 12, and the app crashes on launch with a clear message if one doesn't match (`DeckTest` catches this first).

```json
[
  { "id": "s1-01", "set": 1, "order": 1, "text": "What is a small thing that reliably makes your day better?" }
]
```

## Roadmap

- P1: soft per-set timer, favorites, theme toggle and haptics, resume session, optional closing exercise
- P2: share a card as an image, remote two-device mode (additional decks: done)

See `docs/closer-cards-spec-v1.0.docx` for the full prioritized feature table.

## Attribution

Concept inspired by: Aron, A., Melinat, E., Aron, E. N., Vallone, R. D., & Bator, R. J. (1997). The experimental generation of interpersonal closeness. *Personality and Social Psychology Bulletin*. Citation details to be verified before any public release.

## License

Personal project, not currently open for redistribution. Choose a license (for example MIT) before making the repo public.
