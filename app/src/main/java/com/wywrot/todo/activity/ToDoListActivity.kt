package com.wywrot.todo.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wywrot.todo.R
import com.wywrot.todo.data.FirebaseService
import com.wywrot.todo.data.ToDoItem
import com.wywrot.todo.ui.ToDoItemAdapter
import com.wywrot.todo.ui.ToDoListViewModel
import com.wywrot.todo.utils.Status
import kotlinx.android.synthetic.main.activity_todo_list.*

const val INTENT_REQUEST_CODE = 123
const val TODO_ITEM_TAG = "todoitem"

class ToDoListActivity : AppCompatActivity(), ToDoItemAdapter.CellClickListener {
    lateinit var viewModel: ToDoListViewModel
    private lateinit var adapter: ToDoItemAdapter
    private var isScrolling = false
    private var isLastItemReached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)
        viewModel = ViewModelProvider(this)[ToDoListViewModel::class.java]
        setupViews()
        fetchNewData()
    }

    private fun setupViews() {
        supportActionBar?.title = getString(R.string.your_todos)
        setupSwipeRefresh()
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ToDoItemAdapter(arrayListOf(), this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
        fabButton.setOnClickListener { startCreateToDoActivity() }
        setOnScrollListener()
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.light_purple)
        swipeRefresh.setOnRefreshListener {
            showFetchIndicator()
            fetchNewData(true)
        }
    }

    fun fetchNewData(refreshing: Boolean = false) {
        viewModel.downloadToDoItems(refreshing).observe(this, {
            hideFetchIndicator()
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        bindProgressAndRecyclerViewVisibility(false)
                        resource.data?.let { toDoItems ->
                            if (toDoItems.size < FirebaseService.queryLimit)
                                isLastItemReached = true
                            retrieveList(refreshing, toDoItems)
                        }
                    }
                    Status.ERROR -> {
                        bindProgressAndRecyclerViewVisibility(false)
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> bindProgressAndRecyclerViewVisibility(true)
                }
            }
        })
    }

    private fun deleteItem(toDoItemId: String) {
        viewModel.deleteToDoItem(toDoItemId).observe(this, {
            hideFetchIndicator()
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        bindProgressAndRecyclerViewVisibility(false)
                        resource.data?.let {
                            fetchNewData(true)
                        }
                    }
                    Status.ERROR -> {
                        bindProgressAndRecyclerViewVisibility(false)
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> bindProgressAndRecyclerViewVisibility(true)
                }
            }
        })
    }

    private fun bindProgressAndRecyclerViewVisibility(isLoadingState: Boolean) {
        when (isLoadingState) {
            true -> {
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            else -> {
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun retrieveList(refreshing: Boolean = false, toDoItems: List<ToDoItem>) {
        adapter.apply {
            addToDoItems(refreshing, toDoItems)
            notifyDataSetChanged()
        }
    }

    private fun hideFetchIndicator() = swipeRefresh?.let {
        it.isRefreshing = false
        it.isEnabled = true
    }

    private fun showFetchIndicator() = swipeRefresh?.let { it.isRefreshing = true }

    private fun setOnScrollListener() {
        val onScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val firstVisibleItemPosition =
                        linearLayoutManager!!.findFirstVisibleItemPosition()
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    if (isScrolling && firstVisibleItemPosition + visibleItemCount == totalItemCount && !isLastItemReached) {
                        isScrolling = false
                        fetchNewData(false)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                fetchNewData(true)
            }
        }
    }

    override fun onCellLongClickListener(toDoItem: ToDoItem) {
        showAlertDialog(toDoItem)
    }

    private fun showAlertDialog(toDoItem: ToDoItem) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.deleting_item))
            .setMessage(getString(R.string.deleting_item_message))
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, _ ->
                deleteItem(toDoItem.itemId)
                dialog.dismiss()
                fetchNewData()
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .show()
    }

    override fun onCellClickListener(toDoItem: ToDoItem) = startCreateToDoActivity(toDoItem)

    private fun startCreateToDoActivity(toDoItem: ToDoItem? = null) {
        val intent = Intent(this, CreateToDoActivity::class.java)
        intent.putExtra(TODO_ITEM_TAG, toDoItem)
        startActivityForResult(intent, INTENT_REQUEST_CODE)
    }
}