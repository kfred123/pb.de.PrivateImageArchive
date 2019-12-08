package pia.rest;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.core.Application;
import java.util.Set;

public class MyApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = super.getClasses();
        classes.add(MultiPartFeature.class);
        return classes;
    }
}
