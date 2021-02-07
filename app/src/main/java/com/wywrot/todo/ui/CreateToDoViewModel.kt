package com.wywrot.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.wywrot.todo.data.FirebaseService
import com.wywrot.todo.utils.Resource
import kotlinx.coroutines.Dispatchers

class CreateToDoViewModel : ViewModel() {
    fun uploadToDoItem(editMode: Boolean, toDoItemHashMap: HashMap<String, Any>) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(null))
            if (editMode) {
                when (val response =
                    FirebaseService.updateToDoItem(toDoItemHashMap)) {
                    "" -> emit(Resource.error(null, "Error Occurred!"))
                    else -> emit(Resource.success(response))
                }
            } else {
                when (val response = FirebaseService.addToDoItem(toDoItemHashMap)) {
                    "" -> emit(Resource.error(null, "Error Occurred!"))
                    else -> emit(Resource.success(response))
                }
            }
        }
}