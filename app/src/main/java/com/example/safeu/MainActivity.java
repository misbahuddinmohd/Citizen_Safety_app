package com.example.safeu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String arr[] = {"Strong Password Generator","malicious URL Detector","Emergency SOS", " Password Manager","Guidelines to Stay Safe on Internet"};
    int arrimages[] = {R.drawable.strongpassgen,R.drawable.strongpassgen,R.drawable.strongpassgen,R.drawable.strongpassgen,R.drawable.strongpassgen};
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.customListView);
        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(),arr,arrimages);
        listView.setAdapter(customBaseAdapter);

        // using built in array adapter
        // ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arr);
        // listView.setAdapter(ad);

        //using custom adapter
        //featuresAdapter ad = new featuresAdapter(this, R.layout.featureslayout,arr);
        //listView.setAdapter(ad);

    }

//    public void openActivity(){
//        Toast.makeText(this, "this has worked", Toast.LENGTH_SHORT).show();
//    }
}