package com.example.ps.musicps.Helper;

import android.app.Activity;

import com.example.ps.musicps.View.Dialog.CustomeAlertDialogClass;


public class DialogHelper {
    public static CustomeAlertDialogClass alertDialog;


    public static void alertDialog(final Activity activity, final int action, final String text, CustomeAlertDialogClass.onAlertDialogCliscked onAlertDialogCliscked) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (action) {
                    case 0:
                        alertDialog = new CustomeAlertDialogClass(activity, text, onAlertDialogCliscked);
                        alertDialog.show();
                        break;
                    case 1:
                        alertDialog.dismiss();
                        break;
                }
            }
        });
    }

}
