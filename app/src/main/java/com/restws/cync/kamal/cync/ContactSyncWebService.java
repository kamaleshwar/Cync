package com.restws.cync.kamal.cync;

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

    @Override
    @Get
    public String testGet() {
        return "Working";
    }

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
        }
        return "OK";
    }
}
