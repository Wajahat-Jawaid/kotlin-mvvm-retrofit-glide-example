package com.wajahat.arch.components

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wajahat.arch.components.data.repository.PostsRepository
import com.wajahat.arch.components.ui.post.PostsViewModel
import com.wajahat.arch.components.PostsDataProvider.getDummyPosts
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostsViewModelTest {

    private lateinit var repository: PostsRepository
    private lateinit var viewModel: PostsViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        repository = PostsRepository()
        viewModel = PostsViewModel(repository)
    }

    @Test
    fun `are days different`() {
        val previousPost = getDummyPosts()[0]
        val currentPost = getDummyPosts()[1]
        assertEquals(true, viewModel.isNewDay(previousPost, currentPost))
    }

    @Test
    fun `are days same`() {
        val previousPost = getDummyPosts()[2]
        val currentPost = getDummyPosts()[3]
        assertEquals(false, viewModel.isNewDay(previousPost, currentPost))
    }
}