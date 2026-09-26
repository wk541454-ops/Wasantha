package com.example.auth

object PhoneAuthHelper {

    /**
     * Normalizes a phone number to standard international E.164 format.
     * Defaulting to Sri Lanka (+94) if a local format (e.g. 077..., 77...) is provided.
     */
    fun formatToE164(rawInput: String, defaultCountryCode: String = "+94"): String {
        val trimmed = rawInput.trim().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        if (trimmed.isEmpty()) return ""

        // If already in international format starting with '+'
        if (trimmed.startsWith("+")) {
            return "+" + trimmed.substring(1).replace("[^0-9]".toRegex(), "")
        }

        // If starts with 00 (international call prefix)
        if (trimmed.startsWith("00")) {
            return "+" + trimmed.substring(2).replace("[^0-9]".toRegex(), "")
        }

        val digits = trimmed.replace("[^0-9]".toRegex(), "")

        // Sri Lanka specific formats
        if (defaultCountryCode == "+94") {
            // Local 07x format: 0771234567 -> +94771234567
            if (digits.startsWith("0") && digits.length == 10) {
                return "+94" + digits.substring(1)
            }
            // Local 9-digit format: 771234567 -> +94771234567
            if (!digits.startsWith("0") && digits.length == 9) {
                return "+94$digits"
            }
            // Already has country code digits without plus: 94771234567
            if (digits.startsWith("94") && digits.length == 11) {
                return "+$digits"
            }
        }

        // General fallback: prefix with defaultCountryCode without redundant 0
        val cleanDigits = if (digits.startsWith("0")) digits.substring(1) else digits
        return "$defaultCountryCode$cleanDigits"
    }

    /**
     * Validates whether the formatted phone number is plausible for international telephony.
     */
    fun isValidPhoneNumber(e164Number: String): Boolean {
        if (!e164Number.startsWith("+")) return false
        val digits = e164Number.substring(1)
        // E.164 requires 8 to 15 digits
        if (digits.length !in 8..15 || !digits.all { it.isDigit() }) {
            return false
        }

        // Sri Lanka specific check: +94 followed by 9 digits
        if (e164Number.startsWith("+94")) {
            val slDigits = e164Number.substring(3)
            return slDigits.length == 9 && slDigits.first() in listOf('7', '1', '2', '3', '4', '5', '6', '8', '9')
        }

        return true
    }

    /**
     * Prettifies a phone number for display (e.g. +94 77 123 4567)
     */
    fun formatForDisplay(e164Number: String): String {
        if (e164Number.startsWith("+94") && e164Number.length == 12) {
            val op = e164Number.substring(3, 5)
            val part1 = e164Number.substring(5, 8)
            val part2 = e164Number.substring(8, 12)
            return "+94 $op $part1 $part2"
        }
        return e164Number
    }

    /**
     * Masks a phone number for privacy display (e.g. +94 77 ••• •567)
     */
    fun maskPhoneNumber(e164Number: String): String {
        if (e164Number.length < 7) return e164Number
        val visibleStart = e164Number.take(6)
        val visibleEnd = e164Number.takeLast(3)
        return "$visibleStart ••• •$visibleEnd"
    }
}
