package com.app.rest;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;


public class RestApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Register all REST controllers
        classes.add(AuthenticationRestController.class);
        classes.add(GuestRestController.class);
        classes.add(RoomRestController.class);
        classes.add(ReservationRestController.class);
        classes.add(BillRestController.class);
        
        return classes;
    }
}
