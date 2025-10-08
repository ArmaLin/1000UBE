package com.dyaco.spirit_commercial.support.base_component

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.BindingAdapter

class BindingAdapterComponentKt {

//    @BindingAdapter(
//        "layout_conditionalConstraint_startSide",
//        "layout_conditionalConstraint_toEndId",
//        "layout_conditionalConstraint_endSide",
//        "layout_conditionalConstraint_condition"
//    )
//    fun setConditionalConstraint(
//        view: View, startSide: Int, endId: Int, endSide: Int, condition: Boolean
//    ) {
//        val constraintLayout = (view.parent as? ConstraintLayout) ?: return
//        with(ConstraintSet()) {
//            clone(constraintLayout)
//            if (condition) connect(view.id, startSide, endId, endSide)
//            else clear(view.id, startSide)
//            applyTo(constraintLayout)
//        }
//    }
}