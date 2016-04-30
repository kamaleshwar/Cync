package com.restws.cync.kamal.cync;

import android.content.Context;
import android.util.Log;

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
    private String mSearchTerm;
    private String who;
    private final String protocol = "http://";
    private final String port = "8182";
    private final String path = "/root/cync";


    public Context mContext;

    public CyncWebServiceClient(String name, String searchTerm, String endPoint, Context context) {
        this.mSearchTerm = searchTerm;
        this.endPoint = endPoint;
        this.mContext = context;
        this.who = name;
    }

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
            Log.e(e.getLocalizedMessage(), e.getMessage(), e);
        }
    }

    public void searchContacts() {
        String searchPath = path + "/" + mSearchTerm + ":" + who;
        try {
            ClientResource rService;
            rService = new ClientResource(protocol + endPoint + ":" + port + searchPath);
            String response = rService.get().getText();
            DetailsActivityFragment.showQueryOutput(response);
        } catch (Exception e) {
            Log.e(e.getLocalizedMessage(), e.getMessage(), e);
        }
    }
}
