package com.exgerm.register;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mausv on 12/10/2015.
 */
public class HomepageDrawerAdapter extends ArrayAdapter<HomepageDrawerItem> {
    public HomepageDrawerAdapter(Context context, ArrayList<HomepageDrawerItem> drawerItems) {
        super(context, 0, drawerItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomepageDrawerItem drawerItem = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, parent, false);
        }

        ImageView drawerItemIcon = (ImageView) convertView.findViewById(R.id.imgDrawerIcon);
        TextView drawerItemTitle = (TextView) convertView.findViewById(R.id.tvDrawerItemTitle);

        int drawerIconSet = setIcon(drawerItem.icon);
        drawerItemIcon.setImageResource(drawerIconSet);
        drawerItemTitle.setText(drawerItem.title);

        return convertView;
    }

    private int setIcon(String icon) {
        int drawerIcon = 0;
        switch(icon){
            case "Reportar":
                drawerIcon = R.drawable.drawer_report;
                break;
            case "Altas":
                drawerIcon = R.drawable.drawer_register;
                break;
            case "Posicionar Aparato":
                drawerIcon = R.drawable.drawer_location;
                break;
            case "Bajas":
                drawerIcon = R.drawable.drawer_eliminate;
                break;
            case "Lista de Aparatos":
                drawerIcon = R.drawable.drawer_handset_list;
                break;
            case "Mis Hospitales":
                drawerIcon = R.drawable.drawer_user_hospitals;
                break;
            case "Enviar Pendientes":
                drawerIcon = R.drawable.drawer_send_pending;
                break;
            case "Administrador":
                drawerIcon = R.drawable.drawer_admin;

                break;
        }

        return drawerIcon;
    }
}
