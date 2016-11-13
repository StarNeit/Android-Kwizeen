package com.kwizeen.fooddelivery.app.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Created by admin on 12/18/15.
 */
public class SimpleAlertDialogListener implements DialogInterface.OnClickListener {

    private Context context;
    private Class<?> cls;

    public SimpleAlertDialogListener(Context context, Class<?> cls) {
        this.context = context;
        this.cls = cls;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
}
