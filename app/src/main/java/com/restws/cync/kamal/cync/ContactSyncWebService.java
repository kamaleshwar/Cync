package com.restws.cync.kamal.cync;

import android.util.Log;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * Created by kamal on 18-04-2016.
 */
public class ContactSyncWebService extends ServerResource implements ContactSyncResource{

    private String oldNumber = "oldContactNumber";
    private String newNumber = "newContactNumber";
    private final String searchAttribute = "term";
    public static boolean taken = false;

    @Override
    @Post
    public String postNumberUpdate(Representation updatedContactNumber) {
        try {
            Form recForm = new Form(updatedContactNumber);
            String old = recForm.getFirstValue(oldNumber);
            String updated = recForm.getFirstValue(newNumber);
            String contactName = HomeScreenActivityFragment.updateContact(old,updated);
            HomeScreenActivityFragment.updatePhoneBook(contactName, updated);

        } catch (Exception e) {
            Log.e(e.getLocalizedMessage(), e.getMessage(), e);
        }
        return "OK";
    }

    @Override
    @Get
    public String searchContacts() {
        String response = "";
        ContactSyncWebService.taken = false;
        try {
            String searchTerm = getAttribute(searchAttribute);
            response = HomeScreenActivityFragment.trySearch(searchTerm, false);
            while (HomeScreenActivityFragment.waitForResponse) {
                //dummy loop to wait for the user response.
                // I know this is bad...
            }
            response = HomeScreenActivityFragment.trySearch(searchTerm, true);
            HomeScreenActivityFragment.waitForResponse = true;
        } catch (Exception e) {
            Log.e(e.getLocalizedMessage(), e.getMessage(), e);
        }
        return response;
    }
}
