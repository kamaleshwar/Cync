package com.restws.cync.kamal.cync;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * Created by kamal on 18-04-2016.
 */
public class ContactSyncServer extends Application {

    @Override
    public synchronized Restlet createInboundRoot() {
        Router routerInstance = new Router(getContext());
        routerInstance.attach("/cync",ContactSyncWebService.class);
        routerInstance.attach("/cync/{term}", ContactSyncWebService.class);
        return routerInstance;
    }
}
