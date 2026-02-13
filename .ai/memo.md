# Memo (Compressed)

Date: 2026-02-13

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
- Chat2: diary generation now uses `chat2DiarySystemPrompt`; dialogue is appended into system prompt and `messages` is not sent.
- Chat3: quick replies now rely on system prompt only (`messages = emptyList()`), supports `${LASTRESPONSE}` replacement.
- Quick-reply JSON support: `{ "reply1": "...", "reply2": "..." }`.

## Runtime behavior updates
- Exit chat diary generation fixed for clear-before-generate race:
  - snapshot messages first,
  - clear UI/session immediately,
  - generate diary in background from snapshot,
  - show success dialog only when diary generation succeeds.
- Assistant reply now parses affection marker: `<好感变化:+X>` / `<好感变化:-X>`.
  - Parsed delta is applied via `AdjustRelationshipStateUseCase` to affection stats.
- Added real streaming support for Chat1 with OkHttp + SSE (`stream=true`).
- Added automatic fallback to standard non-streaming mode when SSE is unsupported.
- Streaming UI now shows tail indicator `◐` during generation; removed automatically after done.

## Dependency / DI updates
- Added OkHttp dependency (`com.squareup.okhttp3:okhttp:4.12.0`).
- Added `OkHttpClient` provider in `NetworkModule` for `OpenAICompatibleChatService`.

## UX updates
- Settings page scrollable, prompt inputs height-limited, back button present.
- API actions and Prompt actions split.
- One-tap clear buttons added for Chat1/2/3 prompt fields.
- Chat input anchored at bottom; quick replies no longer overlap message list.
- Added explicit diary entry button in chat/home.
- On exit, show dialog: `Yuki新写了一篇日记，快来看看吧` (only on successful diary generation).

## Multi-player save support
- Added player domain/repository/use-cases.
- Added `player_profile` table + DAO.
- DB upgraded to v5 with `MIGRATION_4_5` and default player init.
- Added DataStore key `active_player_id`.
- Chat and Diary now read/write by active player slot.
- Settings supports create/switch player slots.

## Validation
- Passed:
  - `:app:compileDebugKotlin`

## Next suggested step
- Add tests for SSE path and fallback path in `OpenAICompatibleChatService` (including `[DONE]` and non-SSE content-type fallback).
- Optionally strip control tags (e.g. `<好感变化:+X>`) from displayed/synthesized assistant text while still applying stats.
