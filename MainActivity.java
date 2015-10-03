package com.example.nicholas.hackapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.example.nicholas.hackapp.DigitalLifeController;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public boolean isLightOn;
    public String lightControlDguid;
    public DigitalLifeController dlc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dlc = DigitalLifeController.getInstance();
        dlc.init("EE_E424920D0D768DAF_1", "https://systest.digitallife.att.com");
        try {
            dlc.login( "553474449", "NO-PASSWD");
        } catch (Exception e) {
            System.out.println("Logout Failed");
            e.printStackTrace();
            return;
        }

        JSONArray j_array = dlc.fetchDevices();
        for (int i = 0; i < j_array.size(); i++){
            JSONObject w = (JSONObject)j_array.get(i);
            if(w.get("deviceType")!=null && ((String)w.get("deviceType")).equalsIgnoreCase("light-control")){
                lightControlDguid = (String) w.get("deviceGuid");
                JSONArray attributeArray = (JSONArray)w.get("attributes");
                for(int j = 0; j < attributeArray.size(); j++){
                    JSONObject x = (JSONObject)attributeArray.get(j);
                    System.out.println("label = " + x.get("label") + "value = " + x.get("value"));
                    if (x.get("label").equals("switch") && x.get("value").equals("on")) {
                        isLightOn = true;
                    }
                    else if (x.get("label").equals("switch") && x.get("value").equals("off")){
                        isLightOn = false;
                    }
                }
            }
        }

        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Light is " + isLightOn);
                if (isLightOn == true){
                    dlc.updateDevice(lightControlDguid,"switch", "off");
                    isLightOn = false;
                }
                else{
                    dlc.updateDevice(lightControlDguid, "switch", "on");
                    isLightOn = true;
                }
            }
        });
    }
}
