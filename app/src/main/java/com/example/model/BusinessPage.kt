package com.example.model

data class BusinessPage(
    val id: String,
    val name: String,
    val category: String,
    val imageUrl: String,
    val coverUrl: String = "",
    val bio: String = "",
    val phone: String = "",
    val followers: Int = 1250,
    val unreadMessages: Int = 3,
    val rating: Double = 4.9,
    val reviewCount: Int = 124,
    val website: String = "https://business.friendhub.com",
    val address: String = "123 Business St, Colombo 03, Sri Lanka",
    val hours: String = "Open now: 9:00 AM - 6:00 PM"
)
