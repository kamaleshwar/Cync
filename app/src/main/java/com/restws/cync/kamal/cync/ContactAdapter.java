package com.restws.cync.kamal.cync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by kamal on 06-04-2016.
 */
public class ContactAdapter extends android.support.v4.widget.CursorAdapter {

    public ContactAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView contactName;
        public final TextView contactNumber;
        public final Button callButton;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            contactName = (TextView) view.findViewById(R.id.list_item_contact_name);
            contactNumber = (TextView) view.findViewById(R.id.list_item_contact_number);
            callButton = (Button) view.findViewById(R.id.list_item_call);
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.contacts_list_fragment;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.iconView.setImageResource(R.drawable.contact_logo);

        String contactName = cursor.getString(HomeScreenActivityFragment.COL_CONTACT_NAME);
        viewHolder.contactName.setText(contactName);

        String contactNumber = cursor.getString(HomeScreenActivityFragment.COL_CONTACT_NUMBER);
        viewHolder.contactNumber.setText(contactNumber);

        viewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = viewHolder.contactNumber.getText().toString();
                String absNumber = phoneNumber.substring((phoneNumber.length() - 10), phoneNumber.length());
                String callUri = "tel:" + absNumber.trim();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(callUri));
                context.startActivity(callIntent);
            }
        });

    }
}
