package com.utar.assignment.Util;

import android.content.Context;
import android.widget.Toast;

public class GeneralHelper {
    public static void showMessage(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
