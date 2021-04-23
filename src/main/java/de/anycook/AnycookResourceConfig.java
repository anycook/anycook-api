package de.anycook;

import org.glassfish.jersey.server.ResourceConfig;

public class AnycookResourceConfig extends ResourceConfig {

    public AnycookResourceConfig() {
        packages(this.getClass().getPackageName());
    }
}
