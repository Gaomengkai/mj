package icu.merky.mj.data.remote

import icu.merky.mj.core.coroutine.DispatcherProvider
import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.repository.AIChatService
import icu.merky.mj.domain.repository.ModelApiConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class OpenAICompatibleChatService @Inject constructor(
    private val modelApiConfigRepository: ModelApiConfigRepository,
    private val dispatcherProvider: DispatcherProvider
) : AIChatService {
    private fun logPrompt(tag: String, prompt: String) {
        if (prompt.isBlank()) {
            Log.d(LOG_TAG, "$tag prompt is blank")
            return
        }
        Log.d(LOG_TAG, "$tag prompt:\n$prompt")
    }

    override fun ping(): Flow<AppResult<Unit>> = flow {
        when (val configResult = loadValidatedConfig()) {
            is AppResult.Failure -> emit(configResult)
            is AppResult.Success -> {
                val requestResult = getRequest(
                    url = modelsUrl(configResult.data.endpoint),
                    apiKey = configResult.data.apiKey
                )
                when (requestResult) {
                    is AppResult.Success -> emit(AppResult.Success(Unit))
                    is AppResult.Failure -> emit(requestResult)
                }
            }
        }
    }

    override fun streamReply(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> = flow {
        logPrompt("Chat1", systemPrompt)
        when (val configResult = loadValidatedConfig()) {
            is AppResult.Failure -> emit(configResult)
            is AppResult.Success -> {
                val config = configResult.data
                val payload = buildChatCompletionPayload(
                    model = config.model,
                    messages = messages,
                    systemPrompt = systemPrompt
                )
                when (
                    val requestResult = postRequest(
                        url = chatCompletionsUrl(config.endpoint),
                        apiKey = config.apiKey,
                        body = payload
                    )
                ) {
                    is AppResult.Failure -> emit(requestResult)
                    is AppResult.Success -> {
                        val content = parseAssistantContent(requestResult.data)
                        if (content.isBlank()) {
                            emit(AppResult.Failure(AppError.Network("Model returned empty response.")))
                            return@flow
                        }
                        val chunks = content.split(" ")
                        var aggregate = ""
                        chunks.forEach { token ->
                            aggregate = if (aggregate.isBlank()) token else "$aggregate $token"
                            emit(AppResult.Success(aggregate))
                        }
                    }
                }
            }
        }
    }

    override fun generateDiary(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> =
        flow {
            logPrompt("Chat2", systemPrompt)
            when (val configResult = loadValidatedConfig()) {
                is AppResult.Failure -> emit(configResult)
                is AppResult.Success -> {
                    val config = configResult.data
                    val payload = buildChatCompletionPayload(
                        model = config.model,
                        messages = messages,
                        systemPrompt = systemPrompt
                    )
                    when (
                        val requestResult = postRequest(
                            url = chatCompletionsUrl(config.endpoint),
                            apiKey = config.apiKey,
                            body = payload
                        )
                    ) {
                        is AppResult.Failure -> emit(requestResult)
                        is AppResult.Success -> {
                            val content = parseAssistantContent(requestResult.data)
                            if (content.isBlank()) {
                                emit(AppResult.Failure(AppError.Network("Model returned empty diary.")))
                            } else {
                                emit(AppResult.Success(content.trim()))
                            }
                        }
                    }
                }
            }
        }

    override fun generateQuickReplies(
        messages: List<ChatMessage>,
        systemPrompt: String
    ): Flow<AppResult<List<String>>> = flow {
        logPrompt("Chat3", systemPrompt)
        when (val configResult = loadValidatedConfig()) {
            is AppResult.Failure -> emit(configResult)
            is AppResult.Success -> {
                val config = configResult.data
                val payload = buildChatCompletionPayload(
                    model = config.model,
                    messages = messages,
                    systemPrompt = systemPrompt
                )
                when (
                    val requestResult = postRequest(
                        url = chatCompletionsUrl(config.endpoint),
                        apiKey = config.apiKey,
                        body = payload
                    )
                ) {
                    is AppResult.Failure -> emit(requestResult)
                    is AppResult.Success -> {
                        val content = parseAssistantContent(requestResult.data)
                        if (content.isBlank()) {
                            emit(AppResult.Failure(AppError.Network("Model returned empty quick replies.")))
                        } else {
                            emit(AppResult.Success(parseQuickReplies(content)))
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadValidatedConfig(): AppResult<ModelApiConfig> {
        val config = runCatching {
            modelApiConfigRepository.observeCurrentConfig().first()
        }.getOrElse {
            return AppResult.Failure(AppError.Data("Failed to load model config: ${it.message.orEmpty()}"))
        }

        if (config.endpoint.isBlank()) {
            return AppResult.Failure(AppError.Validation("Model endpoint is not configured."))
        }
        if (config.apiKey.isBlank()) {
            return AppResult.Failure(AppError.Validation("API key is not configured."))
        }
        if (config.model.isBlank()) {
            return AppResult.Failure(AppError.Validation("Model name is not configured."))
        }
        if (!config.endpoint.startsWith("http://") && !config.endpoint.startsWith("https://")) {
            return AppResult.Failure(AppError.Validation("Endpoint must start with http:// or https://"))
        }
        return AppResult.Success(config)
    }

    private suspend fun getRequest(url: String, apiKey: String): AppResult<String> {
        return request(
            method = "GET",
            url = url,
            apiKey = apiKey,
            body = null
        )
    }

    private suspend fun postRequest(url: String, apiKey: String, body: String): AppResult<String> {
        return request(
            method = "POST",
            url = url,
            apiKey = apiKey,
            body = body
        )
    }

    private suspend fun request(
        method: String,
        url: String,
        apiKey: String,
        body: String?
    ): AppResult<String> = withContext(dispatcherProvider.io) {
        runCatching {
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("Authorization", "Bearer $apiKey")
                setRequestProperty("Content-Type", "application/json")
            }

            if (!body.isNullOrBlank()) {
                connection.doOutput = true
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer.write(body)
                }
            }

            val code = connection.responseCode
            val responseBody = readStream(
                stream = if (code in 200..299) connection.inputStream else connection.errorStream
            )
            connection.disconnect()

            if (code in 200..299) {
                AppResult.Success(responseBody)
            } else {
                AppResult.Failure(
                    AppError.Network(
                        "Request failed($code): ${extractErrorMessage(responseBody)}"
                    )
                )
            }
        }.getOrElse { throwable ->
            AppResult.Failure(
                AppError.Network("Network request error: ${throwable.message.orEmpty()}")
            )
        }
    }

    private fun buildChatCompletionPayload(
        model: String,
        messages: List<ChatMessage>,
        systemPrompt: String
    ): String {
        val jsonMessages = JSONArray().apply {
            if (systemPrompt.isNotBlank()) {
                put(
                    JSONObject()
                        .put("role", ChatRole.SYSTEM.name.lowercase())
                        .put("content", systemPrompt)
                )
            }
            messages.forEach { message ->
                put(
                    JSONObject()
                        .put("role", message.role.name.lowercase())
                        .put("content", message.content)
                )
            }
        }

        return JSONObject()
            .put("model", model)
            .put("messages", jsonMessages)
            .put("stream", false)
            .toString()
    }

    private fun parseQuickReplies(raw: String): List<String> {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) {
            return emptyList()
        }

        val jsonReplies = runCatching {
            val json = JSONObject(trimmed)
            listOf(
                json.optString("reply1", "").trim(),
                json.optString("reply2", "").trim()
            ).filter { it.isNotBlank() }
        }.getOrDefault(emptyList())
        if (jsonReplies.isNotEmpty()) {
            return jsonReplies.take(2)
        }

        return trimmed.lines()
            .map { line ->
                line.trim()
                    .removePrefix("- ")
                    .removePrefix("1. ")
                    .removePrefix("2. ")
                    .removePrefix("3. ")
            }
            .filter { it.isNotBlank() }
            .take(2)
    }

    private fun parseAssistantContent(raw: String): String {
        return runCatching {
            val root = JSONObject(raw)
            val choices = root.optJSONArray("choices") ?: return@runCatching ""
            val firstChoice = choices.optJSONObject(0) ?: return@runCatching ""
            val message = firstChoice.optJSONObject("message")
            if (message != null) {
                message.optString("content", "")
            } else {
                firstChoice.optString("text", "")
            }
        }.getOrDefault("")
    }

    private fun extractErrorMessage(raw: String): String {
        return runCatching {
            val root = JSONObject(raw)
            root.optJSONObject("error")?.optString("message")
                ?: root.optString("message")
                ?: raw
        }.getOrDefault(raw)
    }

    private fun readStream(stream: InputStream?): String {
        if (stream == null) {
            return ""
        }
        return BufferedReader(InputStreamReader(stream)).use { reader ->
            buildString {
                var line = reader.readLine()
                while (line != null) {
                    append(line)
                    line = reader.readLine()
                }
            }
        }
    }

    private fun modelsUrl(endpoint: String): String {
        val normalized = endpoint.trim().trimEnd('/')
        return when {
            normalized.endsWith("/v1/models") -> normalized
            normalized.endsWith("/v1") -> "$normalized/models"
            normalized.contains("/v1/chat/completions") -> normalized.replace(
                "/v1/chat/completions",
                "/v1/models"
            )
            else -> "$normalized/v1/models"
        }
    }

    private fun chatCompletionsUrl(endpoint: String): String {
        val normalized = endpoint.trim().trimEnd('/')
        return when {
            normalized.endsWith("/v1/chat/completions") -> normalized
            normalized.endsWith("/v1") -> "$normalized/chat/completions"
            normalized.endsWith("/v1/models") -> normalized.replace("/v1/models", "/v1/chat/completions")
            else -> "$normalized/v1/chat/completions"
        }
    }

    private companion object {
        const val LOG_TAG = "YukiPrompt"
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 30_000
    }
}
