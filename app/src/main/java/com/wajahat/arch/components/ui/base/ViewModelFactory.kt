package com.wajahat.arch.components.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wajahat.arch.components.data.repository.PostsRepository
import com.wajahat.arch.components.ui.post.PostsViewModel

/**
 * Factory class to store the ViewModel references so as they are not recreating every time.
 * */
class ViewModelFactory(private val repository: PostsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}