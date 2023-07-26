package com.securityCode.splashscreen;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList title,textTime,cst;
    LayoutInflater inflter;

    public CustomAdapter(Context context, ArrayList title,ArrayList textTime,ArrayList cst) {
        this.context = context;
        this.title = title;
        this.textTime=textTime;
        this.cst=cst;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return title.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_layout, null);
        TextView titleV = (TextView) view.findViewById(R.id.textView);
        TextView titleC = (TextView) view.findViewById(R.id.textCost);
        TextView titleT = (TextView) view.findViewById(R.id.textTimeDate);
        titleV.setText(title.get(i).toString());
        titleC.setText(cst.get(i).toString());
        titleT.setText(textTime.get(i).toString());
        return view;
    }
}