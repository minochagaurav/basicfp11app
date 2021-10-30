package com.fp.devfantasypowerxi.app.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;


/**
 * Created by MAX on 12/13/2015.
 */

public class CursorAdapter extends SimpleCursorAdapter {

    public CursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


    }


}
