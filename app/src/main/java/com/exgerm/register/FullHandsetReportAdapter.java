package com.exgerm.register;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Mauricio on 8/25/2015.
 */
public class FullHandsetReportAdapter extends ArrayAdapter<FullHandsetReport>{
    public FullHandsetReportAdapter(Context context, ArrayList<FullHandsetReport> handset) {
        super(context, 0, handset);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        FullHandsetReport handset = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_full_handset_list_layout, parent, false);
        }

        TextView user = (TextView) convertView.findViewById(R.id.cHstatusDetailViewUser);
        TextView date = (TextView) convertView.findViewById(R.id.cHstatusDetailViewDate);
        CheckBox physicalDamage = (CheckBox) convertView.findViewById(R.id.cHerror1CB);
        CheckBox physicalRepair = (CheckBox) convertView.findViewById(R.id.cHsolucion1CB);
        CheckBox lowBattery = (CheckBox) convertView.findViewById(R.id.cHerror2CB);
        CheckBox changeBattery = (CheckBox) convertView.findViewById(R.id.cHsolucion2CB);
        CheckBox lowLiquid = (CheckBox) convertView.findViewById(R.id.cHerror3CB);
        CheckBox changeLiquid = (CheckBox) convertView.findViewById(R.id.cHsolucion3CB);
        CheckBox trayClean = (CheckBox) convertView.findViewById(R.id.cHmaint1CB);
        CheckBox machineClean = (CheckBox) convertView.findViewById(R.id.cHmaint2CB);
        TextView comment = (TextView) convertView.findViewById(R.id.cHstatusDetailView);
        CheckBox error = (CheckBox) convertView.findViewById(R.id.cHerroresFinal);
        CheckBox per = (CheckBox) convertView.findViewById(R.id.cHperFinal);

        user.setText(handset.user);
        date.setText(handset.date);
        comment.setText(handset.comment);
        if(handset.physicalDamage == 1) {
            physicalDamage.setChecked(true);
        } else {
            physicalDamage.setChecked(false);
        }
        if(handset.physicalRepair == 1) {
            physicalRepair.setChecked(true);
        } else {
            physicalRepair.setChecked(false);
        }
        if(handset.lowBattery == 1) {
            lowBattery.setChecked(true);
        } else {
            lowBattery.setChecked(false);
        }
        if(handset.changeBattery == 1) {
            changeBattery.setChecked(true);
        } else {
            changeBattery.setChecked(false);
        }
        if(handset.lowLiquid == 1) {
            lowLiquid.setChecked(true);
        } else {
            lowLiquid.setChecked(false);
        }
        if(handset.changeLiquid == 1) {
            changeLiquid.setChecked(true);
        } else {
            changeLiquid.setChecked(false);
        }
        if(handset.trayClean == 1) {
            trayClean.setChecked(true);
        } else {
            trayClean.setChecked(false);
        }
        if(handset.machineClean == 1) {
            machineClean.setChecked(true);
        } else {
            machineClean.setChecked(false);
        }
        if(handset.state == 1) {
            per.setChecked(true);
        } else {
            error.setChecked(false);
        }

        return convertView;
    }

}
