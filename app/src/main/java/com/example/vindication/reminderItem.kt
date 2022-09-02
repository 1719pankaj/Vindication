package com.example.vindication

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class reminderItem(
    val item: String? = null,
    val owner: String? = null,
    val completion: Boolean? = null,
    val date: String? = null,
    val ttid: String? = null
) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}