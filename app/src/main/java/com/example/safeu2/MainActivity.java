package com.example.safeu2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String featurearr[] = {"Malicious URL Detector","Emergency SOS","Strong Password Generator","Password Manager","How to Stay Safe on Internet"};
    int featureimages[] = {R.drawable.malurldet,R.drawable.emergency,R.drawable.strpassgen,R.drawable.passman,R.drawable.knowsupport};

    ListView listView;

    /*---------------for permissions---------------------*/
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 225;
    /*------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*---------------for permissions---------------------*/
        // Check if the user has granted the permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            // Request the permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, REQUEST_CODE);
        } else {
            // The permissions have already been granted, so do whatever you need to do.
            //Log.d(TAG, "Permissions already granted.");
        }
        /*------------------------------------------------------------------*/

        listView = (ListView) findViewById(R.id.customListView);
        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(),featurearr,featureimages);
        listView.setAdapter(customBaseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // Toast.makeText(MainActivity.this, "you have clicked "+i, Toast.LENGTH_SHORT).show();
                if(i==0) {
                    Intent intent = new Intent(MainActivity.this, malUrlDet.class);
                    startActivity(intent);
               // }
                } else if (i==1) {
                    Intent intent = new Intent(MainActivity.this, emergencySos.class);
                    startActivity(intent);
                } else if (i==2) {
                    Intent intent = new Intent(MainActivity.this, strongPassGen.class);
                    startActivity(intent);
                }
                else if (i==3) {
                    Intent intent = new Intent(MainActivity.this, PasswordManager.class);
                    startActivity(intent);
                }
                else if (i==4) {
                    Intent intent = new Intent(MainActivity.this, KnowledgeSupport.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The user has granted the permission.
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                // The user has denied the permission.
                Log.d(TAG, "Permission denied.");
            }
        }
    }
}