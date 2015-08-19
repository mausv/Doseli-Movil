package com.exgerm.register;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Mauricio on 8/19/2015.
 */
public class MissingAdapter extends ArrayAdapter<Missing> {
    public MissingAdapter(Context context, ArrayList<Missing> missing) {
        super(context, 0, missing);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        Missing missing = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_missing_layout, parent, false);
        }

        TextView model = (TextView) convertView.findViewById(R.id.mModel);
        TextView serial_number = (TextView) convertView.findViewById(R.id.mSerial);
        TextView qr = (TextView) convertView.findViewById(R.id.mQr);
        TextView reference = (TextView) convertView.findViewById(R.id.mRef);

        model.setText(missing.model);
        serial_number.setText(missing.serial_number);
        qr.setText(missing.qr);
        reference.setText(missing.reference);

        return convertView;
    }
}
