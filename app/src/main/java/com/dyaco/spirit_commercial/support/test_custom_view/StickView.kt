package com.dyaco.spirit_commercial.support.test_custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.dyaco.spirit_commercial.R

/**
 * Created by Dheeraj Kotwani on 08/07/23
 */
class StickView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    var leftColorData = "#ff0000"
    var rightColorData = "#aaff00"


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(context.getColor(R.color.train))

        val lineColor = Paint()
        lineColor.color = context.getColor(R.color.white)
        lineColor.strokeWidth = 4f
        canvas.drawLine(15f, 200f, 390f, 200f, lineColor)

        val leftColor = Paint()
        leftColor.color = Color.parseColor(leftColorData)
        canvas.drawCircle(15f, 200f, 15f, leftColor)

        val rightColor = Paint()
        rightColor.color = Color.parseColor(rightColorData)
        canvas.drawCircle(385f, 200f, 15f, rightColor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(400, 400)
    }

    fun setColor(first: String, second: String) {
        leftColorData = first
        rightColorData = second
        invalidate()
    }

}


/**
 * import android.animation.Animator
 * import android.os.Bundle
 * import android.util.DisplayMetrics
 * import android.view.Gravity
 * import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
 * import android.widget.LinearLayout
 * import androidx.appcompat.app.AppCompatActivity
 * import kotlin.math.cos
 * import kotlin.math.sin
 * import project.dheerajkotwani.rainbowsticks.databinding.ActivityMainBinding
 *
 * /**
 *  * Created by Dheeraj Kotwani
 *  */
 * class MainActivity : AppCompatActivity() {
 *
 *     private lateinit var binding: ActivityMainBinding
 *     private val list: ArrayList<StickView> = ArrayList()
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         binding = ActivityMainBinding.inflate(layoutInflater)
 *         setContentView(binding.root)
 *
 *         val display = DisplayMetrics()
 *         windowManager.defaultDisplay.getMetrics(display)
 *
 *         val h = display.heightPixels/2
 *         val w = display.widthPixels/2
 *
 *         val colorsList = resources.getStringArray(R.array.rainbow_colors)
 *         val colorsListSize = colorsList.size
 *
 *         for (i in (0 until 24)) {
 *
 *             val view = StickView(this)
 *
 *             view.setColor(colorsList[i%colorsListSize], colorsList[i%colorsListSize])
 *             view.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
 *                 marginStart = w - 200 + (cos(i*(Math.PI/12)) * 240).toInt()
 *                 topMargin = h - 200 + (sin(i*(Math.PI/12)) * 240).toInt()
 *                 gravity = Gravity.CENTER
 *             }
 *             view.rotation = i*15f
 *             binding.root.addView(view)
 *             list.add(view)
 *         }
 *
 *         binding.root.setOnClickListener {
 *             startAnimation3()
 *         }
 *
 *     }
 *
 *     private fun startAnimation1() {
 *         for (view in list) {
 *             view.animate()
 *                 .rotation(view.rotation + 180f)
 *                 .setDuration(2_000L)
 *                 .setListener(object : Animator.AnimatorListener {
 *                     override fun onAnimationStart(animation: Animator) = Unit
 *                     override fun onAnimationEnd(animation: Animator) {
 *                         view.animate()
 *                             .rotation(view.rotation - 180f)
 *                             .setDuration(2_000L)
 *                             .start()
 *                     }
 *
 *                     override fun onAnimationCancel(animation: Animator) = Unit
 *
 *                     override fun onAnimationRepeat(animation: Animator) = Unit
 *
 *                 })
 *                 .start()
 *         }
 *     }
 *
 *     private fun startAnimation2() {
 *         for (view in list) {
 *             view.animate()
 *                 .rotation(view.rotation + 90f)
 *                 .setDuration(1_000L)
 *                 .setListener(object : Animator.AnimatorListener {
 *                     override fun onAnimationStart(animation: Animator) = Unit
 *                     override fun onAnimationEnd(animation: Animator) {
 *                         view.animate()
 *                             .rotation(view.rotation - 90f)
 *                             .setDuration(1_000L)
 *                             .start()
 *                     }
 *
 *                     override fun onAnimationCancel(animation: Animator) = Unit
 *
 *                     override fun onAnimationRepeat(animation: Animator) = Unit
 *
 *                 })
 *                 .start()
 *         }
 *     }
 *
 *     private fun startAnimation3() {
 *         for (position in (0 until list.size)) {
 *             list[position].animate()
 *                 .rotation(list[position].rotation + 180f)
 *                 .setDuration(600L)
 *                 .setStartDelay(position * 150L)
 *                 .start()
 *         }
 *     }
 *
 *     private fun startAnimation4() {
 *         for (position in (0 until list.size)) {
 *             list[position].animate()
 *                 .rotation(list[position].rotation - 180f)
 *                 .setDuration(600L)
 *                 .setStartDelay(position * 150L)
 *                 .start()
 *         }
 *     }
 *
 * }
 */


/**
 *         DisplayMetrics display = new DisplayMetrics();
 * //       WindowManager windowManager = requireActivity().getWindowManager();
 * //       windowManager.getDefaultDisplay().getMetrics(display);
 *
 *         int h = display.heightPixels / 2;
 *         int w = display.widthPixels / 2;
 *
 *         String[] colorsList = getResources().getStringArray(R.array.rainbow_colors);
 *         int colorsListSize = colorsList.length;
 *
 *         List<StickView> list = new ArrayList<>();
 *
 *         for (int i = 0; i < 24; i++) {
 *             StickView view2 = new StickView(requireActivity());
 *
 *             view2.setColor(colorsList[i % colorsListSize], colorsList[i % colorsListSize]);
 *             LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
 *             params.setMarginStart(w - 200 + (int) (Math.cos(i * (Math.PI / 12)) * 240));
 *             params.topMargin = h - 200 + (int) (Math.sin(i * (Math.PI / 12)) * 240);
 *             params.gravity = Gravity.CENTER;
 *             view2.setLayoutParams(params);
 *             view2.setRotation(i * 15f);
 *             getBinding().getRoot().addView(view2);
 *             list.add(view2);
 *         }
 */