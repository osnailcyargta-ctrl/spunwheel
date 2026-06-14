package com.spunwheel.app

import java.util.UUID

data class WheelItem(
    val id: String = UUID.randomUUID().toString(),
    var label: String,
    var color: Int,
    var textColor: Int
)
