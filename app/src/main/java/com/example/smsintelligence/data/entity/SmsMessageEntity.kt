package com.example.smsintelligence.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_messages")
data class SmsMessageEntity(
    @PrimaryKey val id: Long,
    val threadId: Long,
    val sender: String,
    val timestamp: Long,
    val body: String,
    val category: String? = null,
    val subcategory: String? = null,
    val summary: String? = null,
    val amount: Double? = null,
    val currency: String? = "INR",
    val merchant: String? = null,
    val bank: String? = null,
    val accountLast4: String? = null,
    val transactionType: String? = null,
    val transactionId: String? = null,
    val dueDate: String? = null,
    val priority: String? = "low",
    val isProcessed: Boolean = false,
    val requiresAction: Boolean = false,
    val extractedJson: String? = null
)
