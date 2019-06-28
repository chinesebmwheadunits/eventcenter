package io.chinesebmwheadunits.eventcenter;

import com.szchoiceway.eventcenter.EventService;

import java.io.IOException;

import fi.iki.elonen.router.RouterNanoHTTPD;

public class EventCenterHttpServer extends RouterNanoHTTPD {

    private EventService eventService;
    /**
     * Creates a new HTTP server. Used to modify settings in event center.
     */
    public EventCenterHttpServer(EventService eventService) throws IOException {
        super(21323);
        this.eventService = eventService;
        addMappings();
        start();
    }

    @Override
    public void addMappings() {
        super.addMappings();
        addRoute("/settings", SettingsHandler.class, this.eventService.mSysProviderOpt);
    }
}
