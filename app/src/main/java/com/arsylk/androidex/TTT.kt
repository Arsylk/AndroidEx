package com.arsylk.androidex

import android.view.View
import com.arsylk.androidex.lib.ui.adapter.AdaptableRecyclerAdapter

class TTT : AdaptableRecyclerAdapter<Any>() {
    init {
        adapt<Any, View> {

        }
        adapt<Any, View> {
            inflate { layoutInflater, parent, attachToParent -> null!! }
        }
        adaptCustom<Any> {

        }
        adaptCustom<Any> {
            prepareHolder { inflater, viewGroup -> null!! }
        }
    }
}