package com.example.model

import java.util.UUID

/**
 * AdMob Ad Types and Specifications
 */
enum class AdType {
    NATIVE_FEED,
    BANNER,
    INTERSTITIAL,
    REWARDED_VIDEO
}

data class SponsoredAd(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val advertiserName: String,
    val advertiserAvatar: String,
    val headline: String,
    val description: String,
    val mediaUrl: String,
    val ctaText: String = "Install Now",
    val destinationUrl: String = "https://play.google.com",
    val rating: Double = 4.8,
    val priceText: String = "Free",
    val adType: AdType = AdType.NATIVE_FEED,
    val eCpmEst: Double = 12.50
)

/**
 * Creator Analytics Metrics
 */
data class CreatorAnalytics(
    val totalReach: Long = 48500,
    val totalImpressions: Long = 124000,
    val engagementRate: Double = 8.4,
    val peakEngagementHour: String = "8:00 PM - 10:00 PM",
    val topAudienceAge: String = "18-24 (46%), 25-34 (38%)",
    val topAudienceCountry: String = "Sri Lanka (64%), UAE (12%), UK (8%)",
    val totalViewsThisMonth: Long = 89200,
    val sharesCount: Long = 1420
)

/**
 * Virtual Gift & Coin Packages
 */
data class CoinPackage(
    val id: String,
    val coins: Int,
    val bonusCoins: Int = 0,
    val priceUsd: Double,
    val priceLkr: String,
    val badge: String? = null
)

data class VirtualGiftItem(
    val id: String,
    val name: String,
    val emoji: String,
    val coinCost: Int,
    val effectAnimation: String = "GLOW_BURST",
    val creatorEarningsUsd: Double
)

/**
 * Creator Earning & Revenue Sharing Details
 */
data class CreatorEarningWallet(
    val coinBalance: Int = 2450,
    val totalCoinsEarned: Int = 18500,
    val availableCashoutUsd: Double = 129.50,
    val adRevenueShareUsd: Double = 84.20,
    val giftRevenueUsd: Double = 45.30,
    val platformCutPercent: Double = 25.0,
    val creatorSharePercent: Double = 75.0,
    val adRevenueSharePercent: Double = 40.0,
    val lastPayoutDate: String? = "Sep 15, 2026",
    val pendingPayoutUsd: Double = 0.0,
    val isAuditedAndApproved: Boolean = true
)

/**
 * Business Account Subscription Tier
 */
data class BusinessSubscription(
    val isSubscribed: Boolean = false,
    val planName: String = "Creator Business Pro",
    val monthlyPriceUsd: Double = 7.99,
    val renewalDate: String = "Oct 25, 2026",
    val features: List<String> = listOf(
        "Golden VIP Checkmark Badge",
        "Marketplace Top-Priority Listing",
        "Promoted Feed Post Credits (2x / month)",
        "Advanced Demographic Analytics",
        "Direct WhatsApp & Contact CTA on Profile",
        "Priority Customer Support 24/7"
    )
)
