package com.exgerm.register;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mauricio on 8/24/2015.
 */
public class HandsetAdapter extends ArrayAdapter<Handset>{
    public HandsetAdapter(Context context, ArrayList<Handset> handset) {
        super(context, 0, handset);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        Handset handset = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_handset_layout, parent, false);
        }

        TextView handsetM = (TextView) convertView.findViewById(R.id.hHandset);
        TextView location = (TextView) convertView.findViewById(R.id.hLocation);
        TextView reference = (TextView) convertView.findViewById(R.id.hRef);

        handsetM.setText(handset.model + handset.serial_number + " - QR: " + handset.qr);
        location.setText(handset.location);
        reference.setText(handset.reference);

        return convertView;
    }
}
