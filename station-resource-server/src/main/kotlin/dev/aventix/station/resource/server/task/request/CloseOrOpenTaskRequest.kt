package dev.aventix.station.resource.server.task.request

import java.util.UUID

data class CloseOrOpenTaskRequest(
    val id: UUID,
    val state: Boolean
)