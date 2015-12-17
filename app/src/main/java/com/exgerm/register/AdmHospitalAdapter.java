package com.exgerm.register;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mauricio on 8/27/2015.
 */
public class AdmHospitalAdapter extends ArrayAdapter<AdmHospital>{
    public AdmHospitalAdapter(Context context, ArrayList<AdmHospital> handset) {
        super(context, 0, handset);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        AdmHospital hospital = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_hospital_item_layout, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.textViewAdmHospital);
        TextView sellers = (TextView) convertView.findViewById(R.id.tvAdmSellers);
        TextView checked_15 = (TextView) convertView.findViewById(R.id.textViewAdmChecked15);
        TextView missing_15 = (TextView) convertView.findViewById(R.id.textViewAdmMissing15);
        TextView checked_30 = (TextView) convertView.findViewById(R.id.textViewAdmChecked30);
        TextView missing_30 = (TextView) convertView.findViewById(R.id.textViewAdmMissing30);

        name.setText(hospital.name);
        sellers.setText(hospital.sellers);
        checked_15.setText(hospital.checked_15 + "/");
        missing_15.setText(hospital.missing_15);
        checked_30.setText(hospital.checked_30 + "/");
        missing_30.setText(hospital.missing_30);

     return convertView;
    }
}
