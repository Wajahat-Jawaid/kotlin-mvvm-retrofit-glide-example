package com.wajahat.arch.components.utils

import com.wajahat.arch.components.data.model.Post

interface OnPostClickListener {

    fun onPostClicked(post: Post)
}