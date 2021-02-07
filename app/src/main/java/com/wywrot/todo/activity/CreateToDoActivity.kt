package com.wywrot.todo.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.wywrot.todo.R
import com.wywrot.todo.data.ToDoItem
import com.wywrot.todo.ui.CreateToDoViewModel
import com.wywrot.todo.utils.Status
import kotlinx.android.synthetic.main.activity_create_todo_item.*

class CreateToDoActivity : AppCompatActivity() {
    private lateinit var viewModel: CreateToDoViewModel
    private var editMode = false
    private var toDoItemEdited: ToDoItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo_item)

        val toDoItem = intent.getParcelableExtra<ToDoItem>(TODO_ITEM_TAG)
        if (toDoItem != null) {
            toDoItemEdited = toDoItem
            editMode = true
        }
        viewModel = ViewModelProvider(this)[CreateToDoViewModel::class.java]
        setupViews(toDoItem)
    }

    private fun setupViews(toDoItem: ToDoItem?) {
        setSupportActionBar()
        if (toDoItem != null) {
            tietTitle.setText(toDoItem.title)
            tietDescription.setText(toDoItem.description)
        }
        bindButton()
        ivPhoto.setOnClickListener {
            // todo add uploading photos
        }
    }

    private fun setSupportActionBar() {
        with(supportActionBar) {
            title = if (editMode) getString(R.string.edit_item)
            else getString(R.string.add_new_item)

            this?.setDisplayHomeAsUpEnabled(true)
            this?.setDisplayShowHomeEnabled(true)
        }
    }

    private fun bindButton() {
        btnAdd.text = if (editMode) getString(R.string.update_item)
        else getString(R.string.add_item)

        btnAdd.setOnClickListener {
            if (isValid()) {
                uploadToDoItem(createToDoItemToUpload())
            } else {
                Snackbar.make(container, getString(R.string.empty_fields_error_message), 3000)
                    .show()
            }
        }
    }

    private fun uploadToDoItem(toDoItemHashMap: HashMap<String, Any>) {
        viewModel.uploadToDoItem(editMode, toDoItemHashMap).observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        setResult(RESULT_OK, Intent())
                        finish()
                    }
                    Status.ERROR -> progressBar.visibility = View.GONE
                    Status.LOADING -> progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun createToDoItemToUpload(): HashMap<String, Any> {
        val id = if (toDoItemEdited != null) toDoItemEdited!!.itemId else ""
        return hashMapOf(
            "itemId" to id,
            "title" to tietTitle.text.toString(),
            "description" to tietDescription.text.toString(),
            "dateOfCreation" to System.currentTimeMillis(),
            "iconUrl" to ""
        )
    }

    private fun isValid() = (tietTitle.text?.isNotEmpty() ?: false)
            || (tietDescription.text?.isNotEmpty() ?: false)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}