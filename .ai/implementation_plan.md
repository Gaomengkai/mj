# Mj / Yuki Companion 实施计划（V1）

## 0. 当前状态

- 项目目前是 Android Compose 模板工程（单 `:app` 模块）。
- 业务功能尚未落地。
- `AGENTS.md` 已明确技术与约束：Clean Architecture + MVVM + Offline-first，且新增功能遵循 **Domain -> UseCase -> Repository -> UI** 顺序。

---

## 1. 实施原则（执行时必须遵守）

1. 先定义抽象（domain interface），再实现细节（data/local/remote）。
2. 本地可用优先：关键能力（会话、情感状态、配置）即使离线也可运行。
3. 禁止主线程阻塞，统一 `Coroutine + Flow/StateFlow`。
4. 持久化模型一旦发布，不做破坏式修改；变更必须带 migration。
5. 每个里程碑都保持可编译、可测试、可演示。

---

## 2. 目标架构（先在 `:app` 内按包分层，后续可模块化）

建议包结构（与 AGENTS 对齐）：

- `icu.merky.mj.core`
- `icu.merky.mj.domain`
  - `model`
  - `repository`
  - `usecase`
- `icu.merky.mj.data`
  - `local`（Room + DataStore）
  - `remote`（AI API）
  - `repository`
- `icu.merky.mj.feature`
  - `chat`
  - `relationship`
  - `voice`
  - `economy`
  - `wardrobe`
- `icu.merky.mj.ui`

> 说明：先保持单模块，降低启动成本；当功能稳定后再按 feature/data/domain 拆 Gradle 模块。

---

## 3. 分阶段里程碑

## Milestone A：基础骨架（优先）

交付：

- 应用入口从模板页面替换为基础导航壳（可先单页）。
- 建立 domain/data/ui 分层包结构与命名规范。
- 引入并配置：
  - Hilt（依赖注入）
  - Room（本地数据库）
  - DataStore（系统配置）
- 搭建统一 Result/Error 模型与协程调度器抽象。

验收：

- Debug 可运行。
- 至少 1 条 Repository 单元测试可通过。

## Milestone B：关系系统（最小闭环）

按顺序实现：

1. Domain model：`RelationshipState`（affection/trust/mood/updatedAt）。
2. UseCase：读取关系状态、更新关系状态（加减分 + 边界约束）。
3. Repository：`RelationshipRepository` 抽象 + Room 实现。
4. UI：Relationship 面板（只读 + 简单调节按钮）。

验收：

- 状态可持久化。
- 情绪/亲密度规则有单元测试。

## Milestone C：聊天核心（文本版）

按顺序实现：

1. Domain model：`ChatMessage`, `ConversationSession`。
2. UseCase：发送消息、加载历史、流式响应状态机。
3. Repository：
   - `AIChatService` 抽象
   - `ChatRepository`（本地历史 + 远端响应聚合）
4. UI：聊天列表 + 输入框 + 发送流程。

验收：

- 无语音能力时，文本聊天可完整闭环。
- 历史消息本地可恢复。

## Milestone D：语音链路（离线优先）

- 语音输入：Vosk 接入（partial result 流）。
- 语音输出：本地 TTS 引擎抽象（先占位接口，后接具体实现）。
- 音频管线异步化：录音/识别/播报全链路不阻塞主线程。

验收：

- 语音输入文本可注入聊天输入框。
- TTS 可播放文本响应（先基础音色）。

## Milestone E：经济与装扮

- Domain + UseCase + Repository + UI 依序实现：
  - `CurrencyBalance`
  - `Inventory`
  - `OutfitState`
- 增加 idle reward 任务（轻量，不常驻唤醒）。

验收：

- 货币变化可追踪。
- 装扮状态可持久化。

---

## 4. 数据模型首版（建议）

Room Entities 首批：

- `UserProfileEntity`
- `RelationshipStateEntity`
- `ConversationMessageEntity`
- `ConversationSessionEntity`
- `CurrencyBalanceEntity`
- `InventoryItemEntity`
- `OutfitStateEntity`
- `SystemSettingsEntity`（或 DataStore）

说明：

- 会频繁更新、结构化强的数据放 Room。
- 轻量配置（主题、endpoint、开关）优先 DataStore。

---

## 5. 测试策略（从第一天开始）

必须覆盖：

- Repository 测试（fake local/remote）。
- UseCase 测试（尤其情感状态变化规则）。
- 流式聊天状态测试（loading/streaming/success/error）。

建议补充：

- Fake `AIChatService`。
- Fake 语音识别/语音合成引擎，保证 CI 稳定。

---

## 6. 近期执行清单（下一步具体动作）

1. 建立基础分层包结构与空接口（domain/repository/usecase）。
2. 接入 Hilt + Room + DataStore 依赖与初始化。
3. 先完成 Relationship 最小闭环（MVP）。
4. 为 RelationshipUseCase 与 Repository 补齐单元测试。
5. 再进入 Chat 文本闭环开发。

---

## 7. 风险与控制

- 风险：一次性引入过多功能导致架构漂移。
  - 控制：严格里程碑推进，每阶段只做一个闭环。
- 风险：语音模型接入过重，影响启动与包体。
  - 控制：模型外置加载，懒加载，接口先行。
- 风险：早期数据模型频繁改动。
  - 控制：从首版起维护 migration 纪律。

---

## 8. Definition of Done（每个迭代）

- 可编译、可运行。
- 无主线程阻塞。
- 新增核心逻辑有测试。
- 持久化变更有迁移策略。
- 不破坏分层边界。
