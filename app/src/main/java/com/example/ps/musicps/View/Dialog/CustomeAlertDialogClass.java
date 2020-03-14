package com.example.ps.musicps.View.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.ps.musicps.R;

public class CustomeAlertDialogClass extends Dialog implements View.OnClickListener {

    private Activity c;
    public Dialog d;
    private TextView yes;
    private TextView no;
    private TextView tvMessage;
    private String message;
    private onAlertDialogCliscked onAlertDialogCliscked;

    public CustomeAlertDialogClass(Activity a , String message , onAlertDialogCliscked onAlertDialogCliscked) {
        super(a);
        this.c = a;
        this.onAlertDialogCliscked = onAlertDialogCliscked;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog_custom);
        yes = (TextView) findViewById(R.id.tv_AlertDialog_Yes);
        yes.setOnClickListener(this);
        no = (TextView) findViewById(R.id.tv_AlertDialog_No);
        no.setOnClickListener(this);
        tvMessage = findViewById(R.id.tv_Message_AlertDialog);
        tvMessage.setText(message);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_AlertDialog_No:
                dismiss();
                onAlertDialogCliscked.onNegetive();
                break;
                case R.id.tv_AlertDialog_Yes:
                onAlertDialogCliscked.onPosetive();
                break;
        }
        dismiss();
    }

    public interface onAlertDialogCliscked{
        void onPosetive();
        void onNegetive();
    }
}
