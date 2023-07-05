package com.example.safeu2;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.RadioButton;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.safeu2.R;

public class smsReceiver extends BroadcastReceiver {
    String resmalAuto;
    String url;
    String urlapi = "https://malurldet.onrender.com/predict";
    private Context context;

    public smsReceiver() {
        // Default constructor
    }

    public static final String SMS_RECEIVED_ACTION = "com.example.app.SMS_RECEIVED";
    public static final String pdu_type = "pdus";



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Log.i(TAG, "Intent received: " + intent.getAction());

            // Get the SMS message.
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String strMessage = "";
            String format = bundle.getString("format");
            // Retrieve the SMS message received.
            Object[] pdus = (Object[]) bundle.get(pdu_type);
            if (pdus != null) {
                // Check the Android version.
                boolean isVersionM =
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
                // Fill the msgs array.
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    // Check Android version and use appropriate createFromPdu.
                    if (isVersionM) {
                        // If Android version M or newer:
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        // If Android version L or older:
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    // Build the message to show.
                    strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                    strMessage += " :" + msgs[i].getMessageBody() + "\n";
                    // Log and display the SMS message.
                    //Log.d(TAG, "onReceive: " + strMessage);
                    Toast.makeText(context, "smsReceiver message: " + strMessage, Toast.LENGTH_SHORT).show();
                    // Extract the url from the received message
                    url = extractUrl(strMessage);
                    /* ------------------------- getting the value of shared preference here ---------------------------------*/
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    boolean switchState = sharedPreferences.getBoolean("switchState", false);
                    /* ------------------------------------------------------------------------------------------------------ */
                    Toast.makeText(context, "the switch state is "+switchState, Toast.LENGTH_SHORT).show();
                    if (url != null) {
                        /* ---------------------- send the url to Mainactivity or malurldet for their handling -----------------
                        // Create an intent with the custom action
                        Intent dataIntent = new Intent(SMS_RECEIVED_ACTION);
                        // Pass the data as an extra to the intent
                        dataIntent.putExtra("smsData", url);
                        // Send the intent to MainActivity
                        context.sendBroadcast(dataIntent);
                         --------------------------------------------------------------------------------------------------- */
                        Toast.makeText(context, "smsReceiver url: " + url, Toast.LENGTH_LONG).show();
                        //detmalurlforSMS();
                    } else {
                        Toast.makeText(context, "NO URL Found in the message", Toast.LENGTH_LONG).show();
                    }
                }
            }



    }

    public String extractUrl(String messageWithUrl){
        Pattern pattern = Pattern.compile("(?i)\\b((?:https?://|ftp://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))");
        Matcher matcher = pattern.matcher(messageWithUrl);
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public void detmalurlforSMS() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlapi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String res = jsonObject.getString("output");
                            if (res.equals("bad")) {
                                // textView.setText("IT IS MALICIOUS");
                                resmalAuto = "might be Malicious";
                            } else {
                                //textView.setText("IT IS NOT MALICIOUS");
                                resmalAuto = null;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(resmalAuto!=null) {
                            Toast.makeText(context, "smsReceiver(detmalurlforSMS) the link in the message sent to you is " + resmalAuto, Toast.LENGTH_LONG).show();
                            String alertMessage = "The link: '" + url + "' in the message " + resmalAuto;

                            /* --------------- Intent for starting a new activity for showing the AlertDialog -------------------------*/
                            Intent alertIntent = new Intent(context, AlertDialogActivity.class);
                            alertIntent.putExtra("alertMessage", alertMessage);
                            alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(alertIntent);
                            /* --------------------------------------------------------------------------------------------------- */
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("url", url);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}

/* ----------------------------------------------------------------------------------------------
package com.example.safeu2;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class smsReceiver extends BroadcastReceiver {
    String resmalAuto;
    String url = null;


    String urlapi = "https://malurldet.onrender.com/predict";

    private Context context;
    public smsReceiver() {
        // Default constructor
    }

    public static final String SMS_RECEIVED_ACTION = "com.example.app.SMS_RECEIVED";
//    public String msg,mobnum;
//    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
//    private static final String TAG = "SmsBroadcastReceiver";
    private static final String TAG = smsReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";
    private static final String CHANNEL_ID = "notification_channel";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Log.i(TAG, "Intent received: " + intent.getAction());
//        if(intent.getAction()==SMS_RECEIVED)
//        {
//            Bundle databundle = intent.getExtras();
//            if(databundle!=null)
//            {
//                Object[] mypdu = (Object[]) databundle.get("pdus");
//                final SmsMessage[] message = new SmsMessage[mypdu.length];
//
//                for(int i=0;i<mypdu.length;i++)
//                {
//                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
//                    {
//                        String format = databundle.getString("format");
//                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i], format);
//                    }
//                    else {
//                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
//                    }
//                    mobnum = message[i].getDisplayOriginatingAddress();
//                    msg = message[i].getDisplayMessageBody();
//
//                }
//                //Log.d("msgdetails","mobno: "+mobnum+"and"+"msg: "+msg);
//                Toast.makeText(context, "message: "+msg, Toast.LENGTH_SHORT).show();
//            }
//
//        }

        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        //String strMessage="";
        String strMessageBody = "";
        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM =
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                // Build the message to show.
                //strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                //strMessage += " :" + msgs[i].getMessageBody() + "\n";
                strMessageBody += msgs[i].getMessageBody();
                // Log and display the SMS message.
                Log.d(TAG, "onReceive: " + strMessageBody);
                Toast.makeText(context, "received message is "+ strMessageBody, Toast.LENGTH_LONG).show();
                Pattern pattern = Pattern.compile("(?i)\\b((?:https?://|ftp://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))");
                Matcher matcher = pattern.matcher(strMessageBody);
                while (matcher.find()) {
                    url = matcher.group();
                }
                //Intent mainIntent = new Intent(context, MainActivity.class);
                //mainIntent.putExtra("smsBody", url);
                //context.startActivity(mainIntent);
                if(url!=null) {
                    // Create an intent with the custom action
                    Intent dataIntent = new Intent(SMS_RECEIVED_ACTION);
                    // Pass the data as an extra to the intent
                    dataIntent.putExtra("smsData", url);
                    // Send the intent to MainActivity
                    context.sendBroadcast(dataIntent);
                    Toast.makeText(context, url, Toast.LENGTH_LONG).show();
                    //detmalurlforSMS();
                }
            }
        }

    }
    public void detmalurlforSMS() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlapi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String res = jsonObject.getString("output");
                            if (res.equals("good")) {
                                // textView.setText("IT IS NOT MALICIOUS");
                                resmalAuto = "not malicious";
                            } else {
                                //textView.setText("IT IS MALICIOUS");
                                resmalAuto = "malicious";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //textView.setText(resmalAuto);

                        Toast.makeText(context, "the link in the message sent to you is " + resmalAuto, Toast.LENGTH_LONG).show();
                        //String NoteresmalAuto = "the link in the message sent to you is "+resmalAuto;
                        //showNotification(NoteresmalAuto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("url", url);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}
*/


