package com.isw.iswkozen.views.utilViews

import java.util.*

class Greetings {

    companion object {
        fun greet(): String {
            val c = Calendar.getInstance()
            return  when (c.get(Calendar.HOUR_OF_DAY)) {
                in 0..11 -> "Hello, Good Morning"
                in 12..15 -> "Hello, Good Afternoon"
                in 16..21 -> "Hello, Good Evening"
                else -> "Hello, Good day"
            }
        }
    }
}