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
 * Created by mausv on 12/3/2015.
 */
public class UserHospitalsAdapter extends ArrayAdapter<UserHospital>{
    public UserHospitalsAdapter(Context context, ArrayList<UserHospital> userHopsitala) {
        super(context, 0 , userHopsitala);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserHospital userHospital = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_user_hospital, parent, false);
        }

        TextView hospitalName = (TextView) convertView.findViewById(R.id.textUserHospital);
        TextView frequence = (TextView) convertView.findViewById(R.id.textFrequence);
        TextView total = (TextView) convertView.findViewById(R.id.textTotal);
        TextView checked = (TextView) convertView.findViewById(R.id.textChecked);

        hospitalName.setText(userHospital.name);
        frequence.setText(userHospital.frequence);
        total.setText(String.valueOf(userHospital.total));
        checked.setText(String.valueOf(userHospital.checked));

        return convertView;
    }
}
