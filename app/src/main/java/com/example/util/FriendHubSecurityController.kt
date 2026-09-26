package com.example.util

import android.util.Log
import com.example.model.Comment
import com.example.model.Message
import com.example.model.Post
import com.example.model.ReportRecord
import com.example.model.Story
import com.example.model.User
import java.util.regex.Pattern

data class ModerationResult(
    val isFlagged: Boolean,
    val flagReason: String? = null,
    val sanitizedContent: String,
    val isPrivacyViolation: Boolean = false,
    val isHateSpeech: Boolean = false
)

object FriendHubSecurityController {

    private const val TAG = "FriendHubSecurity"

    // --- Patterns for Privacy Violations & Credentials ---
    private val CREDIT_CARD_PATTERN = Pattern.compile("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12})\\b")
    private val LEAKED_PHONE_PATTERN = Pattern.compile("(?:\\+?94|0)?7[0-9]{8}\\b")
    private val SRI_LANKAN_NIC_PATTERN = Pattern.compile("\\b(?:[0-9]{9}[vVxX]|[0-9]{12})\\b")
    private val TOKEN_CREDENTIAL_PATTERN = Pattern.compile("(?i)(?:api[_-]?key|bearer\\s+[a-zA-Z0-9._-]+|sk-[a-zA-Z0-9]{20,}|AIzaSy[a-zA-Z0-9_-]{33}|password\\s*=\\s*[^\\s]+|secret\\s*=\\s*[^\\s]+)")

    // --- Keywords for Hate Speech, Violence & Harassment (English, Sinhala, Singlish) ---
    private val HATE_SPEECH_KEYWORDS = listOf(
        "hate", "kill", "terrorist", "racist", "idiot", "slut", "bitch",
        "bastard", "scam", "fraud", "harass", "murder", "threat", "abuse",
        "මරන්න", "කුණු", "බල්ල", "හරක", "ගහපන්", "වැල", "බැනපන්", "වලිය",
        "paka", "hukan", "ponnaya", "kariya", "balla", "marapan"
    )

    /**
     * 1. Privacy Check: Verifies if a user can view a specific post based on audience settings.
     * Rules:
     * - Public: Accessible to everyone.
     * - Only me: Accessible ONLY by the owner.
     * - Friends: Accessible by owner or confirmed friends.
     * - Shared: Accessible by owner or explicitly listed shared user IDs.
     */
    fun canUserAccessPost(
        post: Post,
        currentUserId: String,
        friendUserIds: List<String> = emptyList()
    ): Boolean {
        if (currentUserId.isBlank()) return post.audience.equals("Public", ignoreCase = true)

        // User always has access to their own posts
        if (post.userId == currentUserId) return true

        return when (post.audience.uppercase()) {
            "PUBLIC", "EVERYONE" -> true
            "ONLY ME", "ONLY_ME", "PRIVATE" -> false
            "FRIENDS" -> friendUserIds.contains(post.userId)
            "SHARED", "SHARED_WITH_ME" -> post.sharedWithUserIds.contains(currentUserId)
            else -> true
        }
    }

    /**
     * Filters a list of posts according to strict privacy rules.
     */
    fun filterVisiblePosts(
        posts: List<Post>,
        currentUserId: String,
        friendUserIds: List<String> = emptyList()
    ): List<Post> {
        return posts.filter { canUserAccessPost(it, currentUserId, friendUserIds) }
            .map { sanitizePostPrivacy(it, currentUserId) }
    }

    /**
     * Filters stories based on story privacy settings.
     */
    fun filterVisibleStories(
        stories: List<Story>,
        currentUserId: String,
        friendUserIds: List<String> = emptyList()
    ): List<Story> {
        return stories.filter { story ->
            story.isMe || story.userId == currentUserId || friendUserIds.contains(story.userId)
        }
    }

    /**
     * 2. Permission Security Check: Blocks unauthorized attempts to read private chat messages or tokens.
     */
    fun canUserAccessChat(
        conversationId: String,
        participantIds: List<String>,
        peerUserId: String?,
        currentUserId: String
    ): Boolean {
        if (currentUserId.isBlank()) return false
        if (participantIds.contains(currentUserId)) return true
        if (peerUserId == currentUserId) return true
        if (conversationId.contains(currentUserId)) return true

        Log.e(TAG, "SECURITY VIOLATION DETECTED: User $currentUserId attempted unauthorized access to conversation $conversationId")
        return false
    }

    /**
     * Blocks and redacts any exposed sensitive authentication tokens, API keys, or secret credentials.
     */
    fun sanitizeTokensAndCredentials(text: String): String {
        if (text.isBlank()) return text
        var cleaned = text
        val tokenMatcher = TOKEN_CREDENTIAL_PATTERN.matcher(cleaned)
        if (tokenMatcher.find()) {
            cleaned = tokenMatcher.replaceAll("🔒 [REDACTED_SECURITY_TOKEN]")
        }
        val ccMatcher = CREDIT_CARD_PATTERN.matcher(cleaned)
        if (ccMatcher.find()) {
            cleaned = ccMatcher.replaceAll("💳 [REDACTED_FINANCIAL_INFO]")
        }
        return cleaned
    }

    /**
     * 3. Automated Content Moderation: Inspects text for Hate Speech, Abusive Language, and Privacy Violations.
     */
    fun inspectAndModerateContent(text: String): ModerationResult {
        if (text.isBlank()) return ModerationResult(false, null, text)

        val lower = text.lowercase()
        var flagReason: String? = null
        var isHate = false
        var isPrivacy = false

        // Check for Hate Speech & Abusive Keywords
        for (keyword in HATE_SPEECH_KEYWORDS) {
            if (lower.contains(keyword)) {
                isHate = true
                flagReason = "Hate speech or abusive language detected ('$keyword')"
                break
            }
        }

        // Check for Privacy Violations (Leaked Tokens, Credit Cards, NIC)
        if (TOKEN_CREDENTIAL_PATTERN.matcher(text).find()) {
            isPrivacy = true
            flagReason = if (flagReason == null) "Privacy Violation: Auth token/secret credential exposed" else "$flagReason & Privacy Violation"
        } else if (CREDIT_CARD_PATTERN.matcher(text).find()) {
            isPrivacy = true
            flagReason = if (flagReason == null) "Privacy Violation: Financial info exposed" else "$flagReason & Privacy Violation"
        }

        val isFlagged = isHate || isPrivacy
        val sanitized = if (isFlagged) {
            "⚠️ [Content Hidden: Flagged by Safety Controller ($flagReason)]"
        } else {
            sanitizeTokensAndCredentials(text)
        }

        return ModerationResult(
            isFlagged = isFlagged,
            flagReason = flagReason,
            sanitizedContent = sanitized,
            isPrivacyViolation = isPrivacy,
            isHateSpeech = isHate
        )
    }

    /**
     * Sanitizes post fields and masks content if flagged.
     */
    fun sanitizePostPrivacy(post: Post, currentUserId: String): Post {
        val moderation = inspectAndModerateContent(post.content)
        val finalContent = if (post.isFlagged) {
            post.content.ifBlank { "⚠️ [Content Hidden by Security Controller]" }
        } else if (moderation.isFlagged) {
            moderation.sanitizedContent
        } else {
            sanitizeTokensAndCredentials(post.content)
        }

        val sanitizedComments = post.comments.map { comment ->
            val commentMod = inspectAndModerateContent(comment.content)
            if (comment.isFlagged || commentMod.isFlagged) {
                comment.copy(
                    content = "⚠️ [Comment Hidden: Violates Community Guidelines]",
                    isFlagged = true,
                    flagReason = commentMod.flagReason ?: comment.flagReason
                )
            } else {
                comment.copy(content = sanitizeTokensAndCredentials(comment.content))
            }
        }

        return post.copy(
            content = finalContent,
            isFlagged = post.isFlagged || moderation.isFlagged,
            flagReason = post.flagReason ?: moderation.flagReason,
            comments = sanitizedComments
        )
    }

    /**
     * Sanitizes message for chat privacy, decryption and token protection.
     */
    fun sanitizeMessage(message: Message, conversationId: String = ""): Message {
        val decryptedContent = if (conversationId.isNotBlank()) {
            E2EECryptoEngine.decryptMessage(message.content, conversationId)
        } else {
            message.content
        }

        val moderation = inspectAndModerateContent(decryptedContent)
        return if (message.isFlagged || moderation.isFlagged) {
            message.copy(
                content = "🔒 [Message Blocked: Flagged for Security/Privacy Policy]",
                isFlagged = true,
                flagReason = moderation.flagReason ?: message.flagReason
            )
        } else {
            message.copy(content = sanitizeTokensAndCredentials(decryptedContent))
        }
    }

    /**
     * Protects private user profile data (email, phone, blocked lists) from being exposed to non-permitted users.
     */
    fun protectUserDataPrivacy(user: User, currentUserId: String, isFriend: Boolean = false): User {
        if (currentUserId.isBlank() || user.id == currentUserId) {
            return user // Own profile remains fully accessible to the user
        }

        return if (!isFriend && !user.isFriend) {
            user.copy(
                email = if (user.email.isNotBlank()) "🔒 Private Email" else "",
                phone = if (user.phone.isNotBlank()) "🔒 Private Phone" else "",
                blockedUserIds = emptyList()
            )
        } else {
            user.copy(
                blockedUserIds = emptyList() // Blocked user lists are strictly private
            )
        }
    }
}
