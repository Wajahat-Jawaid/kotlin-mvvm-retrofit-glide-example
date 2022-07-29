package com.wajahat.arch.components.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.wajahat.arch.components.data.model.Post
import com.wajahat.arch.components.data.repository.PostsRepository
import com.wajahat.arch.components.utils.Resource
import kotlinx.coroutines.Dispatchers

class PostsViewModel(private val repository: PostsRepository) : ViewModel() {

    fun getPosts() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getPosts(POSTS_COUNT)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error!!!"))
        }
    }

    /** Calculate the difference between two post's date */
    fun isNewDay(previousPost: Post, currentPost: Post): Boolean {
        return previousPost.getDateObj().time - DAY_MILLISECONDS > currentPost.getDateObj().time
    }

    companion object {
        // Milliseconds in a single day
        private const val DAY_MILLISECONDS = 60000 * 60 * 24L
        private const val POSTS_COUNT = 10
    }
}