package com.example.ps.musicps.View.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.ps.musicps.R;

public class CustomeDialogClass extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public TextView cancel;

    public CustomeDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        cancel = (TextView) findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
        }
        dismiss();
    }
}
