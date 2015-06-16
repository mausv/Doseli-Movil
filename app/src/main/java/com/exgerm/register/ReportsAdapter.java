package com.exgerm.register;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        TextView dateHomepage = (TextView) convertView.findViewById(R.id.dateHP);
        TextView statusHomepage = (TextView) convertView.findViewById(R.id.statusHP);
        // Populate the data into the template view using the data object
        usernameHomepage.setText(report.username);
        dateHomepage.setText(report.date);
        statusHomepage.setText(report.status);
        // Return the completed view to render on screen
        return convertView;
    }
}
