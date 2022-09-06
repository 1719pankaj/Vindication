package com.example.vindication.dataClass

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class reminderItem(
    val item: String? = null,
    val owner: String? = null,
    val completion: Boolean? = null,
    val date: String? = null,
    val ttid: String? = null
): Parcelable


    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
