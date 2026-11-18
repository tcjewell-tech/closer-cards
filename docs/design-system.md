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
│   │   ├── ReduceMotion.kt   # observes the system "Remove animations" setting
│   │   └── Theme.kt          # CloserCardsTheme, CloserTheme accessors
│   └── components/
│       ├── CloserButton.kt   # primary and secondary pill buttons
│       ├── FlipCard.kt       # the card, face down and revealed
│       ├── TurnBanner.kt     # "<name> answers first"
│       ├── SetProgress.kt    # "Set 2 of 3 · Card 5 of 12" plus pills
│       ├── SetIntro.kt       # set intro and set complete block
│       ├── NameField.kt      # optional player name on Setup
│       ├── FocusRing.kt      # 2dp primary focus ring (a Modifier.Node)
│       └── ComponentPreviews.kt  # Android Studio previews, light and dark
├── res/font/
│   ├── figtree.ttf           # variable, wght
│   └── newsreader.ttf        # variable, opsz + wght
├── res/values/strings.xml    # every user-facing string the components and screens use
├── res/values*/themes.xml    # window background before Compose draws (surface, light and dark)
└── assets/licenses/          # SIL Open Font License for both faces
```

## Using it

`MainActivity` wraps the app once:

```kotlin
setContent {
    CloserCardsTheme {
        Surface(Modifier.fillMaxSize(), color = CloserTheme.colors.surface) {
            CloserCardsNavHost()
        }
    }
}
```

Read tokens with `CloserTheme.colors` and `CloserTheme.typography`, plus the `CloserSpacing`, `CloserShapes` and `CloserSizes` objects. Stock Material 3 widgets pick up the same colours and type through `MaterialTheme`: every colour role is mapped from the tokens (errors use `primary`; the system has no error colour), so none falls back to the baseline purple.

Components take the deck's shape and copy as parameters; none of them knows there are 3 sets of 12. Their fixed labels (Tap to reveal, the position line, the turn banner, accessibility descriptions) come from `strings.xml`.

| Component | Signature (required parameters) |
|---|---|
| `FlipCard` | `set, card, totalCards, question, flipped, onFlip` |
| `SetProgress` | `set, totalSets, card, totalCards` |
| `SetIntro` | `set, totalSets, title, guidance`, optional `passNote`, `complete`, and `actions: ColumnScope.(SetColors) -> Unit` |
| `TurnBanner` | `set, name` |
| `CloserButton` | `text, onClick`, optional `variant`, `enabled`, `onSet` |
| `NameField` | `label, value, onValueChange`, optional `placeholder`, `imeAction` |

Screen recipes (as built in `ui/screens/Screens.kt`):

- **Card:** `SetProgress`, `TurnBanner`, `FlipCard` in the remaining space, then a row with `CloserButton("Next")` and `CloserButton("Skip", variant = Secondary)`. `flipped` lives in the session ViewModel and resets on Next or Skip. Wrap the card in `key(cardId)` so a new card starts face down instead of flipping back over the next question.
- **Set intro and set complete:** one `SetIntro` with its buttons as `actions`. Pass the `SetColors` it hands you to each button as `onSet`: a primary button on a set block becomes a `surfaceRaised` pill with a 1dp border in the set's content colour, a secondary one takes the content colour for text and border. Pass the deck's `passNote` on the intro only.
- **Setup:** two `NameField`s (the second with `ImeAction.Done`), then `CloserButton("Start set 1")`. Blank names fall back to "Player A" and "Player B".

## Accessibility notes

- The face-down card has a 1dp `outline` border: set 1 on the light surface (1.56:1) and set 3 on the dark surface (2.43:1) are below 3:1 on their own.
- With "Remove animations" on, the card swaps faces with no flip. Compose scales every animation by the same system setting, so a crossfade would run in 0ms anyway. The setting is observed, so changing it mid-session takes effect at once.

## Build requirements

Met by `app/build.gradle.kts`: `namespace = "com.tom.closercards"`, `minSdk = 26` (variable font settings), Compose BOM 2026.09.00, `material3`, `ui-tooling-preview`, and `debugImplementation("androidx.compose.ui:ui-tooling")` for previews.
