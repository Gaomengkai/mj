# AGENTS.md

## Project Overview

This project is an Android AI companion chat application.

App name (working): **Yuki Companion**

Primary goals:

* AI conversational companion experience
* Emotional progression system (affinity / trust)
* Local-first inference where possible
* User-configurable AI backend
* Character interaction economy system

Target platform:

* Android (minSdk 26+)
* Kotlin
* Android Studio

Architecture style:

* Clean Architecture
* MVVM
* Offline-first
* Modular design

---

## Core Features

### 1. Chat System

* Multi-turn conversation
* Streaming responses supported
* User-configurable AI API endpoint
* Compatible with OpenAI-style APIs

### 2. Local Speech Recognition

Engine: **Vosk**

Requirements:

* Offline recognition
* On-device inference
* Model hot-switch support
* Partial result streaming

### 3. Local Text-to-Speech

Engine: local neural TTS

Current candidate:

* VITS (if model size acceptable)
* Must support:

    * offline inference
    * voice selection
    * adjustable speed / pitch

IMPORTANT:
TTS model must be modularly loadable and replaceable.

### 4. Relationship System

Persistent emotional state:

* affection
* trust
* mood modifiers
* interaction memory

Storage must be local.

### 5. Economy System

* coins
* idle reward generation
* gift purchase
* cosmetic unlock
* outfit switching

System must support expansion.

---

## Data Storage

Primary storage: **Room database**

Entities:

* UserProfile
* RelationshipState
* ConversationHistory
* Inventory
* CurrencyBalance
* OutfitState
* SystemSettings

Rules:

* No business logic in DAO
* Repository pattern required
* Schema migration mandatory

---

## Networking

AI backend must be abstracted.

Define interface:

AIChatService

Implementations:

* OpenAI compatible
* Custom endpoint
* Local LLM (future)

Do NOT hardcode URLs or API formats.

---

## Audio Pipeline

### Input

Mic → Vosk → text stream → chat engine

### Output

chat response → TTS → audio playback

All processing asynchronous.

No blocking main thread.

---

## Project Structure

app/
ui/
chat/
voice/
relationship/
economy/
wardrobe/
data/
local/
remote/
repository/
domain/
model/
usecase/
core/
audio/
network/
storage/
util/

---

## Coding Standards

Language: Kotlin only.

Required:

* Coroutines for async work
* Flow / StateFlow for reactive state
* Dependency injection (Hilt recommended)
* Immutable data models
* Null safety strictly enforced

Forbidden:

* Global mutable state
* Blocking IO on main thread
* Hardcoded config
* God classes

---

## Performance Requirements

Cold start target:
< 2 seconds

Speech latency target:
< 500ms partial result

Memory:

* Must run on 6GB RAM devices
* TTS models load on demand

Battery:

* Idle reward system must be lightweight
* No constant wake locks

---

## UX Principles

* Character presence must feel continuous
* Emotional state must influence responses
* Voice interaction must feel immediate
* Offline functionality prioritized

---

## Security

* API keys stored encrypted
* No plaintext secrets
* No remote execution of arbitrary code
* Validate all external responses

---

## AI Agent Working Rules

AI can write your memo in .ai folder.

When modifying code:

1. Do not break modular boundaries.
2. Do not introduce new libraries without justification.
3. Always prefer interface abstraction.
4. Maintain offline capability.
5. Preserve data compatibility.
6. Never remove persistence fields without migration.

When adding features:

* implement domain model
* implement use case
* implement repository
* implement UI

Order is mandatory.

---

## Build Instructions

Android Studio Iguana or newer.

Gradle Kotlin DSL.

Build debug:

./gradlew assembleDebug

Run tests:

./gradlew test

---

## Testing Requirements

Required for:

* repositories
* use cases
* emotional state transitions

Preferred:

* fake AI service
* fake speech engine

---

## Model Handling Policy

Large models must be:

* external storage loadable
* versioned
* replaceable without app reinstall

Do not bundle multi-GB models in APK.

---

## Future Expansion Targets

* local LLM fallback
* multi-character support
* memory summarization
* emotion-driven voice synthesis
* cloud sync (optional)

Design must remain extensible.

---

## Definition of Done

A task is complete when:

* builds successfully
* no lint errors
* no main thread blocking
* persistence verified
* architecture respected

---