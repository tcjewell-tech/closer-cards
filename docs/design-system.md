# Design system

The source of truth is the Closer Cards design system artifact: https://claude.ai/artifact/C7VJdsN76ygeCVPo7DVbw8 (private to the owner). It holds the tokens, the brand book, and a live web preview of each component. The Compose code below mirrors it; change a value there first, then here.

## Files

```
app/src/main/
├── java/com/tom/closercards/ui/
│   ├── theme/
│   │   ├── Color.kt          # CloserColors, light and dark values, forSet(set)
│   │   ├── Fonts.kt          # Newsreader and Figtree from res/font
│   │   ├── Type.kt           # CloserTypography and its Material 3 mapping
│   │   ├── Dimens.kt         # CloserSpacing, CloserShapes, CloserSizes, CloserMotion
│   │   ├── ReduceMotion.kt   # reads the system "Remove animations" setting
│   │   └── Theme.kt          # CloserCardsTheme, CloserTheme accessors
│   └── components/
│       ├── CloserButton.kt   # primary and secondary pill buttons
│       ├── FlipCard.kt       # the card, face down and revealed
│       ├── TurnBanner.kt     # "<name> answers first"
│       ├── SetProgress.kt    # "Set 2 of 3 · Card 5 of 12" plus pills
│       ├── SetIntro.kt       # set intro and set complete block
│       ├── NameField.kt      # optional player name on Setup
│       ├── FocusRing.kt      # 2dp primary focus ring
│       └── ComponentPreviews.kt  # Android Studio previews, light and dark
├── res/font/
│   ├── figtree.ttf           # variable, wght
│   └── newsreader.ttf        # variable, opsz + wght
└── assets/licenses/          # SIL Open Font License for both faces
```

## Using it

Wrap the app once, in `MainActivity`:

```kotlin
setContent {
    CloserCardsTheme {
        Surface(color = CloserTheme.colors.surface) { CloserCardsNavHost() }
    }
}
```

Read tokens with `CloserTheme.colors` and `CloserTheme.typography`, plus the `CloserSpacing`, `CloserShapes` and `CloserSizes` objects. Stock Material 3 widgets pick up the same colours and type through `MaterialTheme`.

Screen recipes:

- **Card:** `SetProgress`, `TurnBanner`, `FlipCard`, then a row with `CloserButton("Next")` and `CloserButton("Skip", variant = Secondary)`. Keep `flipped` in the session ViewModel and reset it on Next or Skip.
- **Set intro and set complete:** one `SetIntro` with its buttons as `actions`. The Set 3 pass note is built in.
- **Setup:** two `NameField`s, then `CloserButton("Start set 1")`. Blank names fall back to "Player A" and "Player B".

## Build requirements

The Gradle project isn't set up yet. When it is, this code needs:

- `namespace = "com.tom.closercards"` (Fonts.kt imports `com.tom.closercards.R`)
- `minSdk = 26` (variable font settings)
- Compose BOM 2024.02.00 or newer: Material 3 1.2+ for `HorizontalDivider` and the `surfaceContainer*` colour roles, and `ui-tooling-preview` 1.6+ for `@PreviewLightDark`
- `androidx.compose.material3:material3`, `androidx.compose.ui:ui-tooling-preview`, and `debugImplementation("androidx.compose.ui:ui-tooling")`

This code has not been compiled yet: the environment it was written in had no Android SDK. Expect to fix minor issues on the first build.
