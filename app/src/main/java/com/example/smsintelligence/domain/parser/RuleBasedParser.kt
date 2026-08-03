package com.example.smsintelligence.domain.parser

import com.example.smsintelligence.data.entity.SmsMessageEntity

class RuleBasedParser {
    private val amountRegex = Regex("(?:Rs\\.?|INR|₹)\\s*(\\d+(?:,\\d+)*(?:\\.\\d+)?)", RegexOption.IGNORE_CASE)
    private val accountRegex = Regex("(?:ending|acct|a/c|card|x|\\*)\\s*(\\d{4})", RegexOption.IGNORE_CASE)
    private val upiRegex = Regex("(?:UPI|Ref|Txn|Transaction ID)[:\\s]*([A-Za-z0-9]+)", RegexOption.IGNORE_CASE)

    fun parse(sms: SmsMessageEntity): SmsMessageEntity {
        val body = sms.body
        var category = "Other"
        var amount: Double? = null
        var accountLast4: String? = null
        var transactionType: String? = null
        var transactionId: String? = null

        amountRegex.find(body)?.let {
            amount = it.groupValues[1].replace(",", "").toDoubleOrNull()
        }

        accountRegex.find(body)?.let {
            accountLast4 = it.groupValues[1]
        }

        upiRegex.find(body)?.let {
            transactionId = it.groupValues[1]
        }

        if (body.contains("debited", ignoreCase = true) || body.contains("spent", ignoreCase = true) || body.contains("paid", ignoreCase = true)) {
            transactionType = "debit"
            category = "Finance"
        } else if (body.contains("credited", ignoreCase = true) || body.contains("received", ignoreCase = true)) {
            transactionType = "credit"
            category = "Finance"
        } else if (body.contains("due", ignoreCase = true) || body.contains("bill", ignoreCase = true)) {
            category = "Bill"
        } else if (body.contains("OTP", ignoreCase = true) || body.contains("verification code", ignoreCase = true)) {
            category = "OTP"
        }

        return sms.copy(
            category = category,
            amount = amount,
            accountLast4 = accountLast4,
            transactionType = transactionType,
            transactionId = transactionId,
            isProcessed = true
        )
    }
}
