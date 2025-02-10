package com.kwon.chosungmarket.common.utils

import android.util.Log

class KLog {
    companion object {
        private const val TAG = "KWON_LOG"
        private const val MAX_LOG_LENGTH = 4000
        private const val JSON_INDENT = 2

        var isLoggingEnabled = true
            private set

        fun setLoggingEnabled(enabled: Boolean) {
            isLoggingEnabled = enabled
        }

        fun d(message: String, tag: String = TAG) {
            if (!isLoggingEnabled) return
            log(LogLevel.DEBUG, tag, message)
        }

        fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
            log(LogLevel.ERROR, tag, message, throwable)
        }

        fun i(message: String, tag: String = TAG) {
            if (!isLoggingEnabled) return
            log(LogLevel.INFO, tag, message)
        }

        fun w(message: String, tag: String = TAG) {
            log(LogLevel.WARN, tag, message)
        }

        fun json(json: String?, tag: String = TAG) {
            if (!isLoggingEnabled) return
            if (json.isNullOrEmpty()) {
                e("Empty/Null json content", tag = tag)
                return
            }

            try {
                val prettyJson = formatJson(json)
                d(prettyJson, tag)
            } catch (e: Exception) {
                e("Invalid JSON format: ${e.message}", tag = tag)
            }
        }

        private fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
            if (message.isEmpty()) return

            val stackTrace = Throwable().stackTrace
            val callSite = stackTrace.getOrNull(2) ?: stackTrace.first()

            val fullMessage = buildString {
                append("│ ")
                append("(${callSite.fileName}:${callSite.lineNumber}) ")
                append("[${callSite.methodName}] ")
                append(message)
            }

            if (fullMessage.length > MAX_LOG_LENGTH) {
                val chunkCount = fullMessage.length / MAX_LOG_LENGTH + 1
                for (i in 0 until chunkCount) {
                    val start = i * MAX_LOG_LENGTH
                    val end = kotlin.math.min(start + MAX_LOG_LENGTH, fullMessage.length)
                    printLog(level, tag, if (i == 0) fullMessage.substring(start, end)
                    else "├─ ${fullMessage.substring(start, end)}")
                }
            } else {
                printLog(level, tag, fullMessage)
            }

            throwable?.let {
                e("│ StackTrace: ${Log.getStackTraceString(it)}", tag = tag)
            }
        }

        private fun printLog(level: LogLevel, tag: String, message: String) {
            when (level) {
                LogLevel.DEBUG -> Log.d(tag, message)
                LogLevel.ERROR -> Log.e(tag, message)
                LogLevel.INFO -> Log.i(tag, message)
                LogLevel.WARN -> Log.w(tag, message)
            }
        }

        private fun formatJson(json: String): String {
            val parser = org.json.JSONTokener(json)
            val formatted = when (val jsonObject = parser.nextValue()) {
                is org.json.JSONObject -> jsonObject.toString(JSON_INDENT)
                is org.json.JSONArray -> jsonObject.toString(JSON_INDENT)
                else -> json
            }
            return formatted.replace("\n", "\n│ ")
        }
    }

    private enum class LogLevel {
        DEBUG, ERROR, INFO, WARN
    }
}