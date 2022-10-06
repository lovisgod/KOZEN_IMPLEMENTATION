package com.isw.iswkozen.views.utilViews

import android.view.View
import android.widget.TextView

class Animator {

    companion object {

        fun View.hideMe() {
            if (visibility == View.VISIBLE && alpha == 1f) {
                animate()
                    .alpha(0f)
                    .withEndAction { visibility = View.GONE }
            }
        }

        fun View.showMe() {
            if (visibility == View.GONE && alpha == 0f) {
                animate()
                    .alpha(1f)
                    .withEndAction { visibility = View.VISIBLE }
            }
        }

        fun View.makeVisible() {
            if (visibility == View.GONE) {
                println("this is called")
                animate()
                    .setDuration(250)
                    .withEndAction { visibility = View.VISIBLE }
                    .start()
            }
        }

        fun View.makeHidden() {
            if (visibility == View.VISIBLE) {
                println("this is called")
                animate()
                    .setDuration(250)
                    .withEndAction { visibility = View.GONE }
                    .start()
            }
        }

        fun TextView.changeText(text: String) {
            animate().alpha(0f).setDuration(100).alpha(1f).setDuration(100).withEndAction { setText(text) }.start()
        }
    }
}