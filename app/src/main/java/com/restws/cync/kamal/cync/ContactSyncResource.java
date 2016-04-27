package com.restws.cync.kamal.cync;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Created by kamal on 18-04-2016.
 */
public interface ContactSyncResource {

    @Get
    public String testGet();

    @Post
    public String postNumberUpdate(Representation updatedContactNumber);
}
