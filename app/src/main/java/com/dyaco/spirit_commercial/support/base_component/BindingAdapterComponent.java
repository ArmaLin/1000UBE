package com.dyaco.spirit_commercial.support.base_component;

import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_SPIRIT;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.ObservableInt;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.bumptech.glide.Glide;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class BindingAdapterComponent {
    //    @JvmStatic
    @BindingAdapter("layoutMarginEnd")
    public static void setLayoutMarginEnd(View view, float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMarginEnd((int) dimen);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("layoutMarginStart")
    public static void setLayoutMarginStart(View view, float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMarginStart((int) dimen);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("layoutMarginTop")
    public static void setLayoutMarginTop(View view, float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(0, (int) dimen, 0, 0);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter(value = {"marginTop", "marginBottom", "marginStart", "marginEnd"}, requireAll = false)
    public static void setAllMargin(View view, float top, float bottom, float start, float end) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins((int) start, (int) top, (int) end, (int) bottom);
        view.setLayoutParams(layoutParams);
    }


    @BindingAdapter({"padding", "shouldAdd"})
    public static void setPadding(AppCompatImageView imageView, boolean shouldAdd, int padding) {
        if (shouldAdd) {
            imageView.setPadding(padding, padding, padding, padding);
        }
    }

    @BindingAdapter("layoutMarginBottom")
    public static void setLayoutMarginBottom(View view, float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, (int) dimen);
  //      layoutParams.bottomMargin = (int) dimen;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("layout_width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("layout_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }


    //    app:layout_C_Constraint_EndToStart="@{@id/ibWifi}"
    @BindingAdapter("layout_C_Constraint_EndToStart")
    public static void setC_Constraint_EndToStart(View view, int startSideRes) {
        ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(view.getId(), ConstraintSet.END, startSideRes, ConstraintSet.START, 0);//layout_constraintTop_toBottomOf
        constraintSet.applyTo(constraintLayout);

//        ConstraintLayout constraintLayout = (view.parent as ? ConstraintLayout) ?:return
//                with(ConstraintSet()) {
//            clone(constraintLayout)
//            if (condition) connect(view.id, startSide, endId, endSide)
//            else clear(view.id, startSide);
//            applyTo(constraintLayout);
//        }
    }

//    @BindingAdapter(value = {"imageUrl","placeHolder"},requireAll = false)
//    public static void loadImage(AppCompatImageView view, Drawable url, Drawable placeHolder) {
//        GlideApp.with(view.getContext())
//                .load(url)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .placeholder(placeHolder)
//                .skipMemoryCache(true)
//                .into(view);
//    }

    @BindingAdapter("imageUrl")
    public static void loadImage(AppCompatImageView view, Drawable url) {
        Glide.with(view.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view);
    }

    @BindingAdapter("iTag")
    public static void setSpecialTag(View view, Object value) {
        view.setTag(String.valueOf(value));
    }

    @BindingAdapter("iPosition")
    public static void setPosition(SegmentedButtonGroup view, int value) {
        view.setPosition(value, false);
    }

    @InverseBindingAdapter(attribute = "iPosition")
    public static int setPosition(SegmentedButtonGroup view) {
        return view.getPosition();
    }

    @BindingAdapter("iPositionAttrChanged")
    public static void setPositionChangeListener(SegmentedButtonGroup view, InverseBindingListener inverseBindingListener) {
        view.setOnPositionChangedListener(position -> inverseBindingListener.onChange());
    }


    @BindingAdapter("c_visibility")
    public static void goneUnless(View view, Boolean visible) {
//        view.visibility = visible ? View.VISIBLE : View.GONE;
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("c_textSize")
    public static void bindTextSize(TextView textView, int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }


    @BindingAdapter("setBackgroundTintColor")
    public static void setBackgroundTintColor(MaterialButton view, ColorStateList res) {
        view.setBackgroundTintList(res);
    }

    @BindingAdapter("setCircleRadius")
    public static void setCircleRadius(View view, int size) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.circleRadius = size;
        view.setLayoutParams(layoutParams);
    }


    //    <!--        app:setTextL="@{@string/fatburn}"-->
//<!--        app:locale="@{deviceSetting.locale}"-->
//<!--        android:text="@string/fatburn"-->
    @BindingAdapter(value = {"setTextL", "locale"}, requireAll = false)
    public static void setTextN(TextView view, String text, Locale locale) {
        view.setText(text);
    }

    @BindingAdapter({"srcConditional", "drawableTrue", "drawableFalse"})
    public static void setSrcConditional(AppCompatImageView imageView, Boolean isTreadmill, Drawable drawableTrue, Drawable drawableFalse) {
        if (isTreadmill != null && isTreadmill) {
            imageView.setImageDrawable(drawableTrue);
        } else {
            imageView.setImageDrawable(drawableFalse);
        }
    }


    @BindingAdapter({"bikeRpmText", "deviceSetting"})
    public static void setBikeRpm(AppCompatTextView view, ObservableInt rpmField, DeviceSettingViewModel d) {
        if (rpmField == null) return;

        int rpm = rpmField.get();

        if ((MODE == ModeEnum.CE1000ENT)) {
            rpm *= 2;
        }

        Context context = view.getContext();

        String unit;

        unit = (MODE == ModeEnum.CE1000ENT) ?
                context.getString(R.string.SPM).toLowerCase(Locale.ROOT) : // Elliptical
                context.getString(R.string.rpm); //BIKE


        // ✅ 更新 UI
        view.setText(String.format(Locale.getDefault(), "%d %s", rpm, unit));

        int iconResId;
        if (d.consoleSystem.get() == CONSOLE_SYSTEM_SPIRIT) {
            iconResId = R.drawable.icon_speed_32;
        } else {
//            iconResId = (MODE == ModeEnum.CE1000ENT) ? R.drawable.icon_speed_32 : R.drawable.icon_activity_48;
            iconResId = R.drawable.icon_egym_rpm_36;
        }


        Drawable drawable = ContextCompat.getDrawable(context, iconResId);

        // 🔹 加載圖示並修改顏色
        if (drawable != null) {
            // 設定顏色，這裡假設要變成紅色（你可以改成其他顏色）
            int newColor = ContextCompat.getColor(context, R.color.color5a7085);
            drawable.setTint(newColor); // ✅ 修改顏色
        }

        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

}
