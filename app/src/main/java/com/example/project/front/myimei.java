package com.example.project.front;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class myimei extends Activity {

    TextView imei_number;
    Button get_imei;
    String IMEI_Number_Holder;
    TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myimei);

        imei_number = (TextView)findViewById(R.id.textView1);
        get_imei = (Button)findViewById(R.id.button1);

        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        get_imei.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                IMEI_Number_Holder = telephonyManager.getDeviceId();

                imei_number.setText(IMEI_Number_Holder);
            }
        });

    }

}