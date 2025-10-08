package com.dyaco.spirit_commercial.support.custom_view.placeholder

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.dyaco.spirit_commercial.R

class MockPlaceHolder(context: Context, attrs: AttributeSet?): LinearLayout(context, attrs) {

    var repeact = 1
    var layoutResource = android.R.layout.activity_list_item

    init { init(attrs) }

    private fun init(attrs: AttributeSet?) {

        orientation = VERTICAL

        with(context.theme.obtainStyledAttributes(attrs, R.styleable.MockPlaceHolder, 0, 0)){

            try {

                repeact = getInteger(R.styleable.MockPlaceHolder_repeat, 1)
                layoutResource = getResourceId(R.styleable.MockPlaceHolder_layoutRes, android.R.layout.activity_list_item)

                for (i in 1..repeact){

                    addView(inflate(context, layoutResource, null))
                }

            } finally {  recycle() }

        }

    }

}