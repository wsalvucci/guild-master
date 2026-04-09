package com.example.demo.domain.model.tasks

import com.example.demo.domain.model.items.OutputItemData

data class RequestBoard(
    val requests: List<Request>
)

data class Request(
    val uuid: String,
    val name: String,
    val description: String,
    val tasks: List<Task>,
    val reward: List<OutputItemData>,
)
