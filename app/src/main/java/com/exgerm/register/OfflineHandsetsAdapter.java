package com.exgerm.register;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mausv on 12/16/2015.
 */
public class OfflineHandsetsAdapter extends ArrayAdapter<OfflineHandset> {
    public OfflineHandsetsAdapter(Context context, List<OfflineHandset> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        OfflineHandset offlineHandset = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_handset_layout, parent, false);
        }

        TextView handsetM = (TextView) convertView.findViewById(R.id.hHandset);
        TextView location = (TextView) convertView.findViewById(R.id.hLocation);
        TextView reference = (TextView) convertView.findViewById(R.id.hRef);

        handsetM.setText(offlineHandset.model + offlineHandset.serialNumber + " - QR: " + offlineHandset.qrId);
        location.setText(offlineHandset.location);
        reference.setText(offlineHandset.reference);

        return convertView;
    }
}
