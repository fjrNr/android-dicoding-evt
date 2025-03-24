package com.example.dicodingevent.data.remote.response

import com.google.gson.annotations.SerializedName

data class EventResponse(
	@field:SerializedName("listEvents")
	val listEvents: List<EventItem> = listOf(),

	@field:SerializedName("event")
	val event: EventItem? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)