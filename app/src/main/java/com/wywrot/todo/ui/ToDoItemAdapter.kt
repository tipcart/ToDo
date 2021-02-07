package com.wywrot.todo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wywrot.todo.R
import com.wywrot.todo.data.ToDoItem
import com.wywrot.todo.utils.Utils.getDateTime
import kotlinx.android.synthetic.main.todo_item.view.*
import java.util.*


class ToDoItemAdapter(
    private val toDoItems: ArrayList<ToDoItem>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<ToDoItemAdapter.ToDoItemViewHolder>() {

    class ToDoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(toDoItem: ToDoItem, cellClickListener: CellClickListener) {
            itemView.apply {
                tvDateOfCreation.text = getDateTime(toDoItem.dateOfCreation)
                tvTitle.text = if (toDoItem.title.isNotEmpty()) toDoItem.title
                else context.getString(R.string.without_title)

                if (toDoItem.description.isNotEmpty()) tvDescription.visibility = View.VISIBLE
                tvDescription.text = toDoItem.description

                Glide.with(ivAvatar.context)
                    .load(toDoItem.iconUrl)
                    .placeholder(R.drawable.ic_image_64)
                    .into(ivAvatar)

                setOnClickListener {
                    cellClickListener.onCellClickListener(toDoItem)
                }

                setOnLongClickListener {
                    cellClickListener.onCellLongClickListener(toDoItem)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoItemViewHolder =
        ToDoItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.todo_item, parent, false)
        )

    override fun getItemCount(): Int = toDoItems.size

    override fun onBindViewHolder(holder: ToDoItemViewHolder, position: Int) =
        holder.bind(toDoItems[position], cellClickListener)

    fun addToDoItems(refreshing: Boolean, toDoItem: List<ToDoItem>) {
        this.toDoItems.apply {
            if (refreshing) clear()
            addAll(toDoItem)
        }
    }

    interface CellClickListener {
        fun onCellClickListener(toDoItem: ToDoItem)
        fun onCellLongClickListener(toDoItem: ToDoItem)
    }
}
