package com.example.safeu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    String listarr[];
    int listimages[];
    LayoutInflater inflater;
    public CustomBaseAdapter(Context context, String [] arr, int [] arrimages){
        this.context=context;
        this.listarr=listarr;
        this.listimages=listimages;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listarr.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_custom_list_view,null);
        TextView txtView = (TextView) convertView.findViewById(R.id.textView);
        txtView.setText(listarr[position]);
        ImageView listimg = (ImageView) convertView.findViewById(R.id.image_icon);
        listimg.setImageResource(listimages[position]);
        return convertView;
    }
}
