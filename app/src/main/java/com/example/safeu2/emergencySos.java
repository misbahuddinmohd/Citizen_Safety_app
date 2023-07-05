package com.example.safeu2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safeu2.database.Contact;
import com.example.safeu2.database.dbhandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class emergencySos extends AppCompatActivity {

    Button emergency, add;
    EditText enternumber;
    ListView listView;
    TextView notetextView;

    List<String> phnumbers = new ArrayList<String>();
    ArrayAdapter ad;
    //dbhandler db = new dbhandler(MainActivity.this);
    //List<Contact> allContacts = db.getAllContacts();

    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
    String resaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_sos);

        emergency = findViewById(R.id.emergency);
        enternumber = findViewById(R.id.enternumber);
        add = findViewById(R.id.add);
        listView = findViewById(R.id.listView);
        notetextView = findViewById(R.id.notetextView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        notetextView.setText(" Note:\n 1. Make sure that your location is enabled.\n 2. To add a phone number enter below and press add\n 3. Long Press to Delete");

        //----------------------------------------------------------------------------------------------------------------
        dbhandler db = new dbhandler(emergencySos.this);

        //Contact cnt1 = new Contact();
        //cnt1.setNumber("7993924730000001");
        //db.addContact(cnt1);

        //phnumbers.add("7993924730");
        //phnumbers.add("1234567899");

        //db.deleteContactByName("123");
        //db.deleteContact(3);


        //get all contacts
        List<Contact> allContacts = db.getAllContacts();
        for(Contact contact: allContacts) {
            Log.d("BDContacts", "Id: " + contact.getId() + "\n" + "phnumber: " + contact.getNumber() + "\n");
            phnumbers.add(contact.getNumber());
            //ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_list_item_1,phnumbers);
            //listView.setAdapter(ad);

        }

        ad = new ArrayAdapter(this, android.R.layout.simple_list_item_1,phnumbers);
        listView.setAdapter(ad);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = enternumber.getText().toString();
                Contact cnt = new Contact();
                cnt.setNumber(temp);
                db.addContact(cnt);
                phnumbers.add(temp);
                ad.notifyDataSetChanged();
                //upphnumbers();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int listitem, long l) {

                new AlertDialog.Builder(emergencySos.this)
                        .setTitle("Do you want to remove " + phnumbers.get(listitem)+" from list")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //phnumbers.remove(listitem);
                                db.deleteContactByName(phnumbers.get(listitem));
                                phnumbers.remove(listitem);
                                ad.notifyDataSetChanged();
                                //upphnumbers();

                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();

                return false;
            }
        });

        //----------------------------------------------------------------------------------------------------------------


        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        //String loc = "emergency";
                        getLastLocation();

                    } else {
                        requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 1);
                    }
                }

            }

        });

    }


    //------------------------------------------ for getting location---------------------------------------------------------------
    public void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                Geocoder geocoder = new Geocoder(emergencySos.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    String lat = String.valueOf(addresses.get(0).getLatitude());
                                    String lng = String.valueOf(addresses.get(0).getLongitude());
                                    //String address = String.valueOf(addresses.get(0).getAddressLine(0));
                                    resaddress = "EMERGENCY my locaion: https://www.google.com/maps/place/"+lat+","+lng;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(emergencySos.this, "failed to get loc", Toast.LENGTH_SHORT).show();
                                }
                                for(String numS:phnumbers){
                                    sendSMS(numS);
                                }

                            }

                        }
                    });
        }else{
            askPermission();
        }
    }

    public void askPermission(){
        ActivityCompat.requestPermissions(emergencySos.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "provide permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //------------------------------------------ for getting location---------------------------------------------------------------



    //------------------------------------------ for sending sms---------------------------------------------------------------
    public void sendSMS(String numG){
        String msg = resaddress;
        //String num = phnumber.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numG, null, msg, null, null);
            Toast.makeText(this, "msg sent", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }
    }

    //------------------------------------------ for sending sms---------------------------------------------------------------


}