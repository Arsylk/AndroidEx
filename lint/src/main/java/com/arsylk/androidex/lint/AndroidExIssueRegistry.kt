package com.arsylk.androidex.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.arsylk.androidex.lint.adapter.AdapterCodeDetector

@Suppress("UnstableApiUsage")
class AndroidExIssueRegistry : IssueRegistry() {
    override val issues = listOf(AdapterCodeDetector.ISSUE)

    override val api: Int
        get() = CURRENT_API

    override val minApi: Int
        get() = 8 // works with Studio 4.1 or later; see com.android.tools.lint.detector.api.Api / ApiKt

    // Requires lint API 30.0+; if you're still building for something
    // older, just remove this property.
    override val vendor: Vendor = Vendor(
        vendorName = "Arsylk AndroidEx",
        feedbackUrl = "https://github.com/Arsylk/AndroidEx/issues",
        contact = "https://github.com/Arsylk/AndroidEx"
    )
}
