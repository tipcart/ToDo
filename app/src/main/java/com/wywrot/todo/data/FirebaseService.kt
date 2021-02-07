package com.wywrot.todo.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wywrot.todo.data.ToDoItem.Companion.toToDoItem
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList


object FirebaseService {
    const val queryLimit = 30L
    var lastVisible: DocumentSnapshot? = null
    private const val tag = "FirebaseService"
    private const val collectionPath = "todoitems"
    private const val orderByField = "dateOfCreation"
    private val collection = FirebaseFirestore.getInstance().collection(collectionPath)
    private var query: Query = collection.orderBy(orderByField).limit(queryLimit)

    suspend fun getToDoItems(refreshing: Boolean): List<ToDoItem> {
        if (refreshing) lastVisible = null

        val list = ArrayList<ToDoItem>()
        query
            .startAt(lastVisible)
            .get()
            .addOnSuccessListener { docSnapshot ->
                if (docSnapshot.size() > 0)
                    lastVisible = docSnapshot.documents[docSnapshot.size() - 1]

                for (document in docSnapshot) {
                    if (document != null) {
                        val toDoItem: ToDoItem? = document.toToDoItem()
                        if (toDoItem != null) list.add(toDoItem)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting documents.", exception)
            }
            .await().let {
                return list
            }
    }

    suspend fun addToDoItem(toDoItemHashMap: HashMap<String, Any>): String {
        lateinit var result: String
        collection
            .add(toDoItemHashMap)
            .addOnSuccessListener { reference ->
                Log.d(tag, "DocumentSnapshot added with ID: ${reference.id}")
                result = reference.id
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error adding document", e)
            }
            .await()
        return result
    }

    suspend fun updateToDoItem(toDoItemHashMap: HashMap<String, Any>): String {
        lateinit var result: String
        collection.document(toDoItemHashMap["itemId"].toString())
            .update(toDoItemHashMap)
            .addOnSuccessListener {
                Log.d(tag, "DocumentSnapshot successfully updated")
                result = toDoItemHashMap["itemId"].toString()
            }
            .addOnFailureListener { e -> Log.w(tag, "Error updating document", e) }
            .await()
        return result
    }

    suspend fun deleteToDoItem(toDoItemId: String): String {
        lateinit var result: String
        collection.document(toDoItemId)
            .delete()
            .addOnSuccessListener {
                Log.d(tag, "DocumentSnapshot successfully deleted")
                result = toDoItemId
            }
            .addOnFailureListener { e -> Log.w(tag, "Error deleting document", e) }
            .await()
        return result
    }
}
