# Google Play release plan: Closer Cards

App ID: `com.tom.closercards` (set in `app/build.gradle.kts`, and permanent once the first build is uploaded).
Current version: `versionCode 1`, `versionName "1.0"`.

Google's numbers below were checked on 24 November 2026 against the linked help pages. Google changes these rules from time to time, so recheck the linked page before acting on any number.

**Who does what:**

- **Tom:** anything that needs your identity, money, passwords, signing keys or a Play Console click.
- **Claude:** code, CI, docs and graphics, prepared as PRs for you to review.
- Claude never sees the keystore or its passwords.

---

## 1. Play Console account

**What Google requires** ([Register for a Play Console developer account](https://support.google.com/googleplay/android-developer/answer/6112435?hl=en)):

- A one-time **US$25** registration fee.
- You must be 18 or older.
- Google may ask for a government ID and a credit card, both in your legal name. Prepaid cards are not accepted.
- Personal accounts must also **verify an Android device** through the Play Console mobile app before they can publish.

**The closed-test rule** ([App testing requirements for new personal developer accounts](https://support.google.com/googleplay/android-developer/answer/14151465?hl=en)):

- It applies to personal accounts created **after 13 November 2023**.
- Before you can publish to production, you must run a closed test with **at least 12 testers** who stay opted in for **at least 14 days in a row**.
- A tester who leaves and rejoins restarts their 14 days.
- After the test you apply for production access from the Dashboard. Google says the review "usually takes seven days or less".
- If engagement looks low, Google may ask you to keep testing.

| Tom does this | Claude does this |
|---|---|
| Create the developer account, or confirm you already have one. Pay the US$25 and complete ID and device verification. | Nothing to do here. |
| Turn on 2-Step Verification for the Google account that owns the developer account. | |
| Check the account's creation date. If it's after 13 Nov 2023, the closed test (section 5) is required. An earlier note said your account may be older, but plan for the test until you've confirmed otherwise. | |
| Decide the public developer name and contact email. Both show on the store page. | |

## 2. App signing

**How it works** ([Use Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756?hl=en)):

- **App signing key:** Google creates and keeps it for a new app. Phones trust this key. You never hold it, so you can't lose it.
- **Upload key:** this one is yours. It's a keystore file (`.jks`, RSA 2048-bit or larger) that signs each bundle before you upload it, and it proves the upload came from you.
- **If the upload key is lost or leaked:** you are not locked out. You make a new key and use **Request upload key reset** in Play Console. It takes a while, so back the key up properly anyway.

**The keystore rules:**

- Tom creates it.
- It never goes into git: `*.jks` and `keystore.properties` are already in `.gitignore`.
- It's never pasted into chat.
- It reaches CI only through GitHub encrypted secrets.

There's already a `closer-cards.jks` in the project folder from the sideload build (PR #4), and no `keystore.properties` next to it. For Play, make a **new upload key** that only you have ever known the passwords for. Store it **outside** the project folder.

### Tom: create the upload key

Run this in PowerShell from a folder outside the repo, for example `Documents\Keys`. `keytool` asks for the passwords itself, so they never appear in the command or in history.

```powershell
& "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -genkeypair -v -keystore closer-cards-upload.jks -keyalg RSA -keysize 4096 -validity 10000 -alias upload
```

### Tom: back it up

Keep these four things together in your password manager. Attach the file to the entry, and keep a second copy of the file on an encrypted USB drive or in encrypted cloud storage:

1. `closer-cards-upload.jks` (the file)
2. the keystore password
3. the key alias: `upload`
4. the key password

### Tom: give CI the key

In the GitHub repo, go to **Settings → Secrets and variables → Actions → New repository secret** and add four secrets:

| Secret name | Value |
|---|---|
| `UPLOAD_KEYSTORE_BASE64` | The keystore file as base64 text. The command below copies it to the clipboard; paste it straight into the secret form. |
| `UPLOAD_KEYSTORE_PASSWORD` | keystore password |
| `UPLOAD_KEY_ALIAS` | `upload` |
| `UPLOAD_KEY_PASSWORD` | key password |

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("closer-cards-upload.jks")) | Set-Clipboard
```

Clear the clipboard afterwards. Copying any other text is enough.

### Claude: the CI signing job (a later PR)

- Today CI builds an **unsigned** bundle on every PR and push.
- Claude adds a separate **release** job. It runs only on pushes to `main`, or when you start it by hand, and never on pull requests.
- The job rebuilds the keystore from the secrets, writes a temporary `keystore.properties`, runs `./gradlew bundleRelease`, and saves the signed `app-release.aab` as a downloadable artifact on the workflow run.
- The keystore file is deleted when the job ends, and GitHub masks the secrets in its logs.
- You download the `.aab` from the run and upload it in Play Console. Uploading straight from CI through the Play API is possible later, but not worth setting up for a first release.

## 3. Store listing

**Already in `docs/play-store/`:**

- `listing.md`: app name, short and full description, category (Lifestyle), free, no ads, and the App content answers.
- `privacy-policy.md`: the policy says nothing is collected.
- `icon-512.png` and `feature-graphic.png` (1024×500).
- `screenshots/`: 8 phone screenshots.

**Still missing or open:**

| Item | Who | Notes |
|---|---|---|
| **Public privacy-policy URL** | Tom decides, Claude builds | Play needs a public link, even for apps that collect nothing. This repo is private, and GitHub Pages on a private repo needs a paid plan. **Proposal:** a separate small public repo, e.g. `closer-cards-privacy`, holding only the policy as a GitHub Pages page. Claude sets it up after you OK making it public. Google Sites is a no-code alternative. |
| Privacy link inside the app | Claude | A small "Privacy policy" link on the Home or About screen that opens the public page in the browser. The app still needs no internet permission, because the browser does the loading. |
| Retake the Setup screenshot | Claude | The current one predates the timer picker. |
| Developer contact email | Tom | Shown publicly on the listing. |
| Final app name | Tom | "Closer Cards" is still marked as a working title in the original brief. |
| Aron et al. (1997) citation | Tom | The README says to verify it before public release. The listing mentions the study. |

## 4. Play Console forms (App content page)

These answers match how the app works today: no internet permission, no accounts, no ads, nothing stored.

**Content rating (IARC questionnaire)** ([Content ratings](https://support.google.com/googleplay/android-developer/answer/9898843?hl=en)):

- Every app needs one. Apps without a rating are removed. It's free.
- Redo it whenever the content changes.
- The answers below are what to choose. The console's exact wording may differ.

| Question area | Answer |
|---|---|
| App category | Not a game; pick the general or "other" app type, not social or communication |
| Violence, blood, fear or horror | No |
| Sexual content or nudity | No (a keyword scan of `cards.json` found no sexual, drug or alcohol words) |
| Profanity or crude humour | No |
| Drugs, alcohol, tobacco | No |
| Gambling or simulated gambling | No |
| Users can talk to each other or share content online | No (there's no network at all) |
| Shares the user's location | No |
| Digital purchases | No |

Expect the lowest age rating, such as IARC 3+ or Everyone. That's normal: the rating describes the content, while target audience (below) says who the app is for.

**Target audience** ([Target audience and content](https://support.google.com/googleplay/android-developer/answer/9867159?hl=en)):

- Select **18 and over** only. That matches the privacy policy, which says the app is written for adults.
- The extra **restrict minor access** checkbox isn't needed for this app. Leave it off.
- Keep the graphics adult-looking. Google may reject an 18+ app whose art looks aimed at children. The current icon and screenshots are fine.
- Google may take up to 7 days to review this declaration.

**Data safety** ([Data safety section](https://support.google.com/googleplay/android-developer/answer/10787469?hl=en)):

- Google says apps that collect no data must still fill in the form and link a privacy policy.
- Answer **No** to "collects or shares user data".
- Apps that are only on internal testing don't need the form yet. It's needed before the closed test.

**The rest of App content:**

| Item | Answer |
|---|---|
| Ads | No |
| App access | All features available without sign-in |
| Advertising ID | Not used |
| Government, financial, health, news | None apply |

| Tom does this | Claude does this |
|---|---|
| Fill in each form in Play Console using the answers above. | Keep these answers in step with the code, and flag it when a change would alter an answer, such as adding internet access. |

## 5. Testing tracks and rollout order

| Step | What happens | Who | Typical time |
|---|---|---|---|
| 0. Prep | Account verified. Keystore made and backed up. Secrets added. Privacy page live. CI signing job, in-app privacy link and new screenshot merged. | Tom and Claude | 2–5 days (ID checks can be slow) |
| 1. Internal test | Upload the first signed `.aab` and add yourself (and your partner) as testers. Install from Play and play a full session on a real phone. | Tom uploads and tests; Claude fixes what you find | 1–3 days |
| 2. Closed test | Fill in the store listing and all App content forms. Create a closed track and invite **12+ testers** (friends and family with Google accounts, joining by an opt-in link). Ask them to really open it a few times; Google looks at engagement. Keep everyone opted in for **14 straight days**. | Tom recruits and manages; Claude ships fixes as new builds (each with a higher `versionCode`) | 14–16 days |
| 3. Apply for production | Answer Google's three-part questionnaire (about the test, the app, readiness). | Tom | Up to ~7 days for Google to decide |
| 4. Production | Promote the tested build to production. Full rollout is fine for a first 1.0 with no users yet; a staged rollout (e.g. 20%) is optional. The first release is reviewed before it goes live. | Tom | A few days of review |

**Realistic total:**

- With the closed-test rule: about **4–5 weeks** from today, with most of it spent waiting on the 14 days and the reviews.
- If your account turns out to be exempt: about **1–2 weeks** (internal test, then a short closed test anyway, then production).

**Every upload needs a higher `versionCode`.** Claude bumps it in each release PR.

## 6. After Dark (later, separate)

- **Separate app and listing:** After Dark ships later with its own app ID (for example `com.tom.closercards.afterdark`) and its own store listing. It doesn't share a listing or app ID with Closer Cards, so its content and rating can't affect the main app.
- **Code first:** its code is on Tom's laptop and not in git yet. Step one is a private repo for it.
- **Its own setup:** it gets its own upload key, the same backup routine, its own content rating (Mature expected), and target audience **18 and over only** with **restrict minor access** turned on.
- **In-app check:** an 18+ confirmation screen before any content.
- **The policy limit:** Google Play doesn't allow sexually explicit content, whatever the age gate or audience. The [Inappropriate Content policy](https://support.google.com/googleplay/android-developer/answer/9878810?hl=en) bans pornography and content intended to be sexually gratifying. It also bans adult or sexual keywords in the store listing or the app. The 18+ settings only control who can install it, not what's allowed. Every card and every word of the listing must stay suggestive-at-most, within that policy, and gets reviewed against it before submission.
- **Testing:** plan on a fresh closed test for After Dark too.

---

## Tom's to-do list

1. Create or confirm the Play Console account: US$25, ID and device verification, 2-Step Verification on. Check the account's creation date against 13 Nov 2023.
2. Create the upload keystore with the command above and back it up (file + passwords + alias).
3. Add the four GitHub secrets.
4. Decide where the privacy policy is hosted (separate public repo or Google Sites). Choose the contact email, and confirm the app name and the Aron et al. citation.
5. Line up 12+ testers who'll install it and open it a few times over two weeks.
6. In Play Console: create the app, fill in the listing and the App content forms (section 4), and upload builds to internal, then closed, then production.

## Claude's to-do list (each its own PR, on your go-ahead)

1. CI release job that signs the `.aab` from the GitHub secrets.
2. Public privacy-policy page, after you OK a public repo.
3. In-app "Privacy policy" link.
4. Retake the Setup screenshot with the timer picker.
5. A `versionCode` bump for each upload.
