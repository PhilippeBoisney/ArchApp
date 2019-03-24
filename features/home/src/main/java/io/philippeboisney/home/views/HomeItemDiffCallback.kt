package io.philippeboisney.home.views

import androidx.recyclerview.widget.DiffUtil
import io.philippeboisney.model.User

class HomeItemDiffCallback(private val oldList: List<User>,
                           private val newList: List<User>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].avatarUrl == newList[newItemPosition].avatarUrl
                && oldList[oldItemPosition].login == newList[newItemPosition].login
    }
}