package com.wywrot.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.wywrot.todo.data.FirebaseService
import com.wywrot.todo.utils.Resource
import kotlinx.coroutines.Dispatchers

class ToDoListViewModel : ViewModel() {

    //unit test -> sprawdzam czy wywoluje sie emit z loading, wywolac emit success + error w osobnym tescie
    fun downloadToDoItems(refreshing: Boolean) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(FirebaseService.getToDoItems(refreshing)))
        } catch (exception: Exception) {
            emit(Resource.error(null, exception.message ?: "Error Occurred!"))
        }
    }

    fun deleteToDoItem(toDoItemId: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(null))
            when (val response =
                FirebaseService.deleteToDoItem(toDoItemId)) {
                "" -> emit(Resource.error(null, "Error Occurred!"))
                else -> emit(Resource.success(response))
            }
        }
}