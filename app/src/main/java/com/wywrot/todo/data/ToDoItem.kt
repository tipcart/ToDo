package com.wywrot.todo.data

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

const val TAG = "ToDoItem"

@Parcelize
data class ToDoItem(
    val itemId: String,
    val title: String,
    val description: String,
    val dateOfCreation: Long,
    val iconUrl: String = ""
) : Parcelable {

    override fun toString(): String {
        return "title $title description $description"
    }

    companion object {
        fun DocumentSnapshot.toToDoItem(): ToDoItem? {
            return try {
                val title = getString("title")!!
                val description = getString("description")!!
                val dateOfCreation = getLong("dateOfCreation")!!
                val iconUrl = getString("iconUrl")!!
                ToDoItem(id, title, description, dateOfCreation, iconUrl)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting user profile", e)
                null
            }
        }
    }
}