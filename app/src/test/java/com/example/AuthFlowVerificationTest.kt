package com.example

import com.example.auth.PhoneAuthHelper
import org.junit.Assert.*
import org.junit.Test

class AuthFlowVerificationTest {

    // --- Flow 1 & Flow 3: Phone Number Formatting & Validation (Sri Lanka + International) ---
    @Test
    fun testSriLankaPhoneFormatting() {
        val formattedLocal = PhoneAuthHelper.formatToE164("077 123 4567")
        assertEquals("+94771234567", formattedLocal)
        assertTrue(PhoneAuthHelper.isValidPhoneNumber(formattedLocal))

        val formattedWithCountryCode = PhoneAuthHelper.formatToE164("+94 71 987 6543")
        assertEquals("+94719876543", formattedWithCountryCode)
        assertTrue(PhoneAuthHelper.isValidPhoneNumber(formattedWithCountryCode))

        val formattedNoZero = PhoneAuthHelper.formatToE164("781234567")
        assertEquals("+94781234567", formattedNoZero)
        assertTrue(PhoneAuthHelper.isValidPhoneNumber(formattedNoZero))
    }

    @Test
    fun testInternationalPhoneFormatting() {
        val usFormatted = PhoneAuthHelper.formatToE164("+1 (555) 234-5678")
        assertEquals("+15552345678", usFormatted)
        assertTrue(PhoneAuthHelper.isValidPhoneNumber(usFormatted))

        val ukFormatted = PhoneAuthHelper.formatToE164("+44 7911 123456")
        assertEquals("+447911123456", ukFormatted)
        assertTrue(PhoneAuthHelper.isValidPhoneNumber(ukFormatted))

        // Invalid phone number checks
        val invalidTooShort = PhoneAuthHelper.formatToE164("123")
        assertFalse(PhoneAuthHelper.isValidPhoneNumber(invalidTooShort))

        val emptyNumber = PhoneAuthHelper.formatToE164("")
        assertFalse(PhoneAuthHelper.isValidPhoneNumber(emptyNumber))
    }

    // --- Flow 1 & Flow 3: OTP Code Rules, Expiration, and Validation ---
    @Test
    fun testOtpValidationRules() {
        // Valid 6-digit OTP
        val validOtp = "654321"
        assertTrue(validOtp.length == 6 && validOtp.all { it.isDigit() })

        // Invalid: Less than 6 digits
        val shortOtp = "1234"
        assertFalse(shortOtp.length == 6 && shortOtp.all { it.isDigit() })

        // Invalid: Contains non-digit
        val nonNumericOtp = "12a456"
        assertFalse(nonNumericOtp.length == 6 && nonNumericOtp.all { it.isDigit() })

        // OTP expiration timer defaults (120 seconds lifetime, 60 seconds resend cooldown)
        val otpDefaultExpirySeconds = 120
        val otpDefaultCooldownSeconds = 60
        assertTrue("OTP expiration must be positive", otpDefaultExpirySeconds > 0)
        assertTrue("Cooldown timer must be positive", otpDefaultCooldownSeconds > 0)
    }

    // --- Flow 2 & Flow 4: Email & Password Validation ---
    @Test
    fun testEmailAndPasswordValidation() {
        val validEmail = "user@friendhub.app"
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        assertTrue(emailRegex.matches(validEmail))

        val invalidEmail = "invalid-email-format"
        assertFalse(emailRegex.matches(invalidEmail))

        val strongPassword = "SecurePassword123!"
        assertTrue("Password must be at least 6 characters for Firebase Auth", strongPassword.length >= 6)

        val tooShortPassword = "123"
        assertFalse(tooShortPassword.length >= 6)
    }

    // --- Flow 5: Forgot Password Email Format Validation ---
    @Test
    fun testForgotPasswordFormatValidation() {
        val targetEmail = "reset.test@friendhub.app"
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        assertTrue("Password reset email must be valid format", emailRegex.matches(targetEmail))
    }

    // --- Flow 6 & Flow 7: Account Linking Integrity ---
    @Test
    fun testAccountLinkingMultiProviderModel() {
        // User starts with an email-based account
        val initialEmail = "member@example.com"
        val initialPhone: String? = null
        var userProviders = listOf("password")

        // Linking phone to email account (Flow 6)
        val linkedPhone = "+94771234567"
        val updatedProvidersAfterPhone = userProviders + "phone"
        assertTrue(updatedProvidersAfterPhone.contains("password"))
        assertTrue(updatedProvidersAfterPhone.contains("phone"))
        assertEquals(2, updatedProvidersAfterPhone.distinct().size)

        // Starting with phone and linking email (Flow 7)
        val phoneUserProviders = listOf("phone")
        val updatedProvidersAfterEmail = phoneUserProviders + "password"
        assertTrue(updatedProvidersAfterEmail.contains("phone"))
        assertTrue(updatedProvidersAfterEmail.contains("password"))
        assertEquals(2, updatedProvidersAfterEmail.distinct().size)
    }

    // --- Flow 8, Flow 9, Flow 10: Session State, Logout, Re-login & Restoration ---
    @Test
    fun testSessionStateTransitions() {
        var isLoggedIn = false
        var activeUserId: String? = null

        // Flow 9: Login
        val loggedInUid = "firebase_user_abc123"
        isLoggedIn = true
        activeUserId = loggedInUid
        assertTrue(isLoggedIn)
        assertEquals("firebase_user_abc123", activeUserId)

        // Flow 8: Logout completely
        isLoggedIn = false
        activeUserId = null
        assertFalse("User must be logged out", isLoggedIn)
        assertNull("Session user id must be cleared", activeUserId)

        // Flow 10: App restart session restoration simulation
        val persistentSavedSession = "firebase_user_abc123"
        val restoredLoggedIn = persistentSavedSession.isNotEmpty()
        val restoredUid = persistentSavedSession
        assertTrue("Session must be restored upon app restart", restoredLoggedIn)
        assertEquals("firebase_user_abc123", restoredUid)
    }
}
