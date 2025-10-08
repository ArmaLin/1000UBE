package com.dyaco.spirit_commercial.support.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.dyaco.spirit_commercial.R

class NumberSelect : LinearLayout {

    private lateinit var addButton: Button
    private lateinit var minusButton: Button
    private lateinit var valueTextView: TextView

    //最小值
    private var minValue: Int = 0
    //最大值
    private var maxValue: Int = 0
    //預設值
    private var defaultValue: Int = 0
    //目前數值
    var textValue: Int = 0

    private var listener: NumberSelectListener? = null

    interface NumberSelectListener {
        fun onValueChange(value: Int)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.test_number_select, this)
        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        this.addButton = findViewById(R.id.addButton)
        this.minusButton = findViewById(R.id.minusButton)
        this.valueTextView = findViewById(R.id.valueTextView)

        this.textValue = 0
        this.maxValue = Integer.MAX_VALUE
        this.minValue = 0

        if (attrs != null) {
            val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.NumberSelect,
                0, 0
            )


            //從Layout上 取得預設值
            this.maxValue = attributes.getInt(R.styleable.NumberSelect_max_value_xxx, this.maxValue)
            this.minValue = attributes.getInt(R.styleable.NumberSelect_min_value_xxx, this.minValue)
            this.defaultValue = attributes.getInt(R.styleable.NumberSelect_default_value, 0)


            this.valueTextView.text = defaultValue.toString()
            this.textValue = defaultValue
        }

        //點下「+」Button，將TextValue數字+1，並呼叫listener.onValueChange
        this.addButton.setOnClickListener {
            addTextValue()
            if (listener != null) {
                listener!!.onValueChange(textValue)
            }
        }

        //點下「-」Button，將TextValue數字-1，並呼叫listener.onValueChange
        this.minusButton.setOnClickListener {
            minusTextValue()
            if (listener != null) {
                listener!!.onValueChange(textValue)
            }
        }
    }

    fun setMaxValue(value: Int) {
        this.maxValue = value
    }

    fun setMinValue(value: Int) {
        this.minValue = value
    }

    fun setDefaultValue(value: Int) {
        this.defaultValue = value
        this.textValue = value
    }

    private fun addTextValue() {
        if (this.textValue < this.maxValue) {
            this.textValue++
            this.valueTextView.text = this.textValue.toString()
        }
    }

    private fun minusTextValue() {
        if (this.textValue > this.minValue) {
            this.textValue--
            this.valueTextView.text = this.textValue.toString()
        }
    }

    fun setListener(listener: NumberSelectListener) {
        this.listener = listener
    }
}