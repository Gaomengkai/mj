# Memo (Compressed)

Date: 2026-02-11

## Toolchain lock (stable matrix)
- AGP `8.5.2`
- Gradle `8.7`
- Kotlin `1.9.24`
- KSP `1.9.24-1.0.20`
- Hilt `2.51.1`
- Room `2.6.1`
- JDK `17`

## Completed features
- Added OpenAI-compatible model settings page (endpoint/key/model), with test + save.
- Added prompt config storage for Chat1/Chat2/Chat3.
- Added successful endpoint history (recent records) in settings.
- Wired chat runtime to OpenAI-compatible backend.
- Added diary page and diary entry navigation.

## Chat orchestration
- Chat1: main conversation with memory-aware system prompt (from diary).
- Chat2: generate diary on chat exit.
- Chat3: generate 2 lazy replies after assistant response.
- Chat3 supports `${LASTRESPONSE}` replacement (recent two rounds).
- Quick-reply JSON support: `{ "reply1": "...", "reply2": "..." }`.

## UX updates
- Settings page scrollable, prompt inputs height-limited, back button present.
- API actions and Prompt actions split.
- One-tap clear buttons added for Chat1/2/3 prompt fields.
- Chat input anchored at bottom; quick replies no longer overlap message list.
- Added explicit diary entry button in chat/home.
- On exit, show dialog: `Yuki新写了一篇日记，快来看看吧`.

## Multi-player save support
- Added player domain/repository/use-cases.
- Added `player_profile` table + DAO.
- DB upgraded to v5 with `MIGRATION_4_5` and default player init.
- Added DataStore key `active_player_id`.
- Chat and Diary now read/write by active player slot.
- Settings supports create/switch player slots.

## Prompt visibility policy
- Chat1 system prompt is not inserted as visible user message.
- Chat2 currently sends without system prompt.

## Validation
- Passed:
  - `:app:compileDebugKotlin`
  - `:app:compileDebugUnitTestKotlin`

## Next suggested step
- If needed, evolve from `sessionId == playerId` to full model (`player_id + multi-sessions per player`).
