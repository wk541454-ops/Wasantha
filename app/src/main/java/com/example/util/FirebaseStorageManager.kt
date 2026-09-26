package com.example.util

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await

object FirebaseStorageManager {
    private const val TAG = "FirebaseStorageManager"

    private val storage: FirebaseStorage?
        get() = try { FirebaseStorage.getInstance() } catch (e: Exception) { null }

    private val auth: FirebaseAuth?
        get() = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }

    const val MAX_PROFILE_IMAGE_SIZE = 10 * 1024 * 1024L // 10 MB
    const val MAX_POST_MEDIA_SIZE = 50 * 1024 * 1024L   // 50 MB
    const val MAX_CHAT_ATTACHMENT_SIZE = 25 * 1024 * 1024L // 25 MB

    suspend fun uploadProfileImage(
        uri: Uri,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> {
        val st = storage ?: return Result.failure(Exception("Firebase Storage not available"))
        val userId = auth?.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        val ref = st.reference.child("users/$userId/profile_${System.currentTimeMillis()}.jpg")
        return uploadFileInternal(ref, uri, "image/jpeg", MAX_PROFILE_IMAGE_SIZE, onProgress)
    }

    suspend fun uploadCoverImage(
        uri: Uri,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> {
        val st = storage ?: return Result.failure(Exception("Firebase Storage not available"))
        val userId = auth?.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        val ref = st.reference.child("users/$userId/cover_${System.currentTimeMillis()}.jpg")
        return uploadFileInternal(ref, uri, "image/jpeg", MAX_PROFILE_IMAGE_SIZE, onProgress)
    }

    suspend fun uploadPostMedia(
        uri: Uri,
        mimeType: String = "image/jpeg",
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> {
        val st = storage ?: return Result.failure(Exception("Firebase Storage not available"))
        val userId = auth?.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        val ext = if (mimeType.contains("video")) "mp4" else "jpg"
        val ref = st.reference.child("posts/$userId/post_${System.currentTimeMillis()}.$ext")
        return uploadFileInternal(ref, uri, mimeType, MAX_POST_MEDIA_SIZE, onProgress)
    }

    suspend fun uploadStoryMedia(
        uri: Uri,
        mimeType: String = "image/jpeg",
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> {
        val st = storage ?: return Result.failure(Exception("Firebase Storage not available"))
        val userId = auth?.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        val ext = if (mimeType.contains("video")) "mp4" else "jpg"
        val ref = st.reference.child("stories/$userId/story_${System.currentTimeMillis()}.$ext")
        return uploadFileInternal(ref, uri, mimeType, MAX_POST_MEDIA_SIZE, onProgress)
    }

    suspend fun uploadChatAttachment(
        chatId: String,
        uri: Uri,
        mimeType: String,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> {
        val st = storage ?: return Result.failure(Exception("Firebase Storage not available"))
        val userId = auth?.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
        val ext = when {
            mimeType.contains("video") -> "mp4"
            mimeType.contains("audio") -> "mp3"
            else -> "jpg"
        }
        val ref = st.reference.child("chats/$chatId/${userId}_${System.currentTimeMillis()}.$ext")
        return uploadFileInternal(ref, uri, mimeType, MAX_CHAT_ATTACHMENT_SIZE, onProgress)
    }

    private suspend fun uploadFileInternal(
        ref: com.google.firebase.storage.StorageReference,
        uri: Uri,
        mimeType: String,
        maxSizeBytes: Long,
        onProgress: ((Float) -> Unit)?
    ): Result<String> {
        return try {
            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            val uploadTask = ref.putFile(uri, metadata)

            if (onProgress != null) {
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                    onProgress(progress)
                }
            }

            uploadTask.await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Upload failed for ${ref.path}: ${e.message}", e)
            Result.failure(e)
        }
    }
}
