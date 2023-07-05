package com.example.safeu2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class malUrlDet extends AppCompatActivity {
    /* ------ these are for URL from smsReceiver in case if we want to handle it here -------
    private BroadcastReceiver receiver;
    public String smsBodyRes;
    -------------------------------------------------------------------------------------- */

    EditText editText;
    Button button;
    TextView textView;
    TextView textView2;
    String urlapi = "https://malurldet.onrender.com/predict";
    String resmalManual;
    String resmalAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mal_url_det);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        button = findViewById(R.id.button);

        textView2.setText("Note:\nMachine Learning model is being used to detect whether the URL is malicious or not.\nAccuracy of the model may differ.");

        /*----------- getting id of switch and updating shared preference of switch when ever it is changed------------------------*/
        Switch detectSwitch = findViewById(R.id.detectSwitch);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve the stored switch state from shared preferences
        boolean switchState = sharedPreferences.getBoolean("switchState", false);
        // Set the switch state based on the retrieved value
        detectSwitch.setChecked(switchState);

        // handle whenever the switch is changed
        detectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("switchState", isChecked);
                editor.apply();
            }
        });
        /*----------------------------------------------------------------------------------------------------*/


        /* ---------- Getting the URL from smsReceiver in case if we want to handle it here ------------
        IntentFilter filter = new IntentFilter(smsReceiver.SMS_RECEIVED_ACTION);
        // Create a BroadcastReceiver to handle the received intent
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract the data from the intent
                smsBodyRes = intent.getStringExtra("smsData");
                textView.setText(smsBodyRes);
                Toast.makeText(malUrlDet.this, "we have  received the url as: "+smsBodyRes, Toast.LENGTH_LONG).show();
                // Do something with the received data
                // For example, update the UI
                //detmalurlforSMS();
                //textView.setText(smsBodyRes);
            }
        };
        // Register the BroadcastReceiver with the intent filter
        registerReceiver(receiver, filter);
        -----------------------------------------------------------------------------------------------  */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hit the api
                detmalurlforManual();

            }
        });

    }

    public void detmalurlforManual(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlapi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String res = jsonObject.getString("output");
                            if(res.equals("bad")){
                                // textView.setText("IT IS MALICIOUS");
                                resmalManual = "MIGHT BE MALICIOUS";
                            }else{
                                //textView.setText("IT IS NOT MALICIOUS");
                                resmalManual = "MIGHT NOT BE MALICIOUS";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        textView.setText(resmalManual);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(malUrlDet.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("url",editText.getText().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(malUrlDet.this);
        queue.add(stringRequest);
    }


}


/*
public class malUrlDet extends AppCompatActivity {
    private BroadcastReceiver receiver;
    public String smsBodyRes;

    EditText editText;
    Button button;
    TextView textView;
    String urlapi = "https://malurldet.onrender.com/predict";
    String resmalManual;
    String resmalAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mal_url_det);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        //smsReceiver message12 = new smsReceiver();
        //String ourmsg = message12.msg;
        //Intent receiveIntent = getIntent();
        //smsBodyRes = receiveIntent.getStringExtra("smsBody");
        //textView2.setText(smsBodyRes);
        // Create an intent filter with the custom action

        IntentFilter filter = new IntentFilter(smsReceiver.SMS_RECEIVED_ACTION);
        // Create a BroadcastReceiver to handle the received intent
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract the data from the intent
                smsBodyRes = intent.getStringExtra("smsData");
                textView.setText(smsBodyRes);
                Toast.makeText(malUrlDet.this, "we have  received the url as: "+smsBodyRes, Toast.LENGTH_LONG).show();
                // Do something with the received data
                // For example, update the UI
                detmalurlforSMS();
                //textView2.setText(smsBodyRes);


            }
        };
        // Register the BroadcastReceiver with the intent filter
        registerReceiver(receiver, filter);

        @Override
        protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(receiver);
    }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hit the api
                detmalurlforManual();
                textView.setText(resmalManual);

            }
        });


    }

    public void detmalurlforSMS(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlapi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String res = jsonObject.getString("output");
                            if(res.equals("good")){
                                // textView.setText("IT IS NOT MALICIOUS");
                                resmalAuto = "not malicious";
                            }else{
                                //textView.setText("IT IS MALICIOUS");
                                resmalAuto = "malicious";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        textView.setText(resmalAuto);
                        Toast.makeText(malUrlDet.this, "the link in the message sent to you is "+resmalAuto, Toast.LENGTH_LONG).show();
                        //String NoteresmalAuto = "the link in the message sent to you is "+resmalAuto;
                        //showNotification(NoteresmalAuto);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(malUrlDet.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("url",smsBodyRes);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(malUrlDet.this);
        queue.add(stringRequest);
    }

    public void detmalurlforManual(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlapi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String res = jsonObject.getString("output");
                            if(res.equals("good")){
                                // textView.setText("IT IS NOT MALICIOUS");
                                resmalManual = "not malicious";
                            }else{
                                //textView.setText("IT IS MALICIOUS");
                                resmalManual = "malicious";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(malUrlDet.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("url",editText.getText().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(malUrlDet.this);
        queue.add(stringRequest);
    }

    private void showNotification(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(malUrlDet.this);
        builder.setTitle("Notification");
        builder.setMessage("Message: " + message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
 */
