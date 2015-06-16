/*
package com.exgerm.register;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

*/
/**
 * Created by Mauricio on 23/12/2014.
 *//*

public class StatusAdapter extends ArrayAdapter<ParseObject>{
    protected Context mContext;
    protected List<ParseObject> mStatus;

    public StatusAdapter(Context context, List <ParseObject> status){
        super(context, R.layout.custom_homepage_layout, status);
        mContext = context;
        mStatus = status;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.custom_homepage_layout, null);
            holder = new ViewHolder();
            holder.usernameHomepage = (TextView) convertView
                    .findViewById(R.id.usernameHP);
            holder.dateHomepage = (TextView) convertView
                    .findViewById(R.id.dateHP);
            holder.statusHomepage = (TextView) convertView
                    .findViewById(R.id.statusHP);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject statusObject = mStatus.get(position);

        String username = statusObject.getString("User");
        String aparato = statusObject.getString("aparato");
        String funcion = statusObject.getString("estado");
        holder.usernameHomepage.setText(username + " - " + aparato + " " + funcion);

        */
/*String estadoFuncion = statusObject.getString("estado");
        if(estadoFuncion == "Funciona"){
            holder.statusHomepage.setTextColor(0xFF00B006);
            holder.statusHomepage.setText(status);
        } else if (estadoFuncion == "Errores") {
            holder.statusHomepage.setTextColor(0xFFA91304);
            holder.statusHomepage.setText(status);
        }*//*


        String fecha = statusObject.getString("fecha");
        holder.dateHomepage.setText(fecha);

        String status = statusObject.getString("Status");
        holder.statusHomepage.setText(status);

        return convertView;
    }

    public static class ViewHolder {
        TextView usernameHomepage;
        TextView dateHomepage;
        TextView statusHomepage;
    }

}
*/
