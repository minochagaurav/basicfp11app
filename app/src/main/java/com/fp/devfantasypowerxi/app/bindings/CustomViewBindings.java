package com.fp.devfantasypowerxi.app.bindings;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class CustomViewBindings {

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }


}
