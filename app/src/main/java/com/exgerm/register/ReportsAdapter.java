package com.exgerm.register;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Mauricio on 11/06/2015.
 */
public class ReportsAdapter extends ArrayAdapter<Report> {
    public ReportsAdapter(Context context, ArrayList<Report> reports) {
        super(context, 0, reports);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Report report = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_homepage_layout, parent, false);
        }
        // Lookup view for data population
        TextView usernameHomepage = (TextView) convertView.findViewById(R.id.usernameHP);
        TextView handsetHomepage = (TextView) convertView.findViewById(R.id.handsetView);
        TextView referenceHomepage = (TextView) convertView.findViewById(R.id.reference);
        TextView dateHomepage = (TextView) convertView.findViewById(R.id.dateHP);
        TextView statusHomepage = (TextView) convertView.findViewById(R.id.statusHP);
        ImageView imgHomepage = (ImageView) convertView.findViewById(R.id.statusView);
        // Populate the data into the template view using the data object
        usernameHomepage.setText(report.username);
        handsetHomepage.setText(report.model + report.serial + " - QR ID: " + report.qrs_id);
        referenceHomepage.setText(report.reference);
        dateHomepage.setText(report.date);
        statusHomepage.setText(report.status);
        Log.i("State: ", report.state);
        if(report.state.equals("1")) {
            imgHomepage.setImageResource(R.drawable.good);
        } else if (report.state.equals("0")) {
            imgHomepage.setImageResource(R.drawable.bad);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
