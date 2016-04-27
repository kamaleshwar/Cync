package com.restws.cync.kamal.cync;

import android.content.Context;
import android.widget.Toast;

import org.restlet.data.Form;
import org.restlet.resource.ClientResource;

/**
 * Created by kamal on 19-04-2016.
 */
public class CyncWebServiceClient {

    private final String newContactKey = "newContactNumber";
    private final String oldContactKey = "oldContactNumber";
    private String newContact = null;
    private String endPoint = null;
    private String oldContact = null;
    private Context mContext;
    private final String protocol = "http://";
    private final String port = "8182";
    private final String path = "/root/cync";
    private final String errorMessage = "Cannot contact the Contact right now ";

    public CyncWebServiceClient(Context context, String oldContact, String newContact, String endPoint) {
        this.newContact = newContact;
        this.endPoint = endPoint;
        this.oldContact = oldContact;
        this.mContext = context;
    }

    public void init() {
        Form form = new Form();
        form.add(newContactKey, newContact);
        form.add(oldContactKey, oldContact);

        try {
            ClientResource rService = new ClientResource(protocol + endPoint + ":"+ port + path);
            rService.post(form);
        } catch (Exception e) {
            Toast.makeText(mContext, errorMessage , Toast.LENGTH_SHORT).show();
        }
    }
}
