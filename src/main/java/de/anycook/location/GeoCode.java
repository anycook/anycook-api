package de.anycook.location;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

import java.io.IOException;
import java.util.List;

public class GeoCode {
    private final Geocoder geocoder;

    public GeoCode() {
        this.geocoder = new Geocoder();
    }

    public Location getLocation(String place) throws IOException, LocationNotFoundException {
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(place).getGeocoderRequest();
        GeocodeResponse geocodeResponse = geocoder.geocode(geocoderRequest);

        List<GeocoderResult> results = geocodeResponse.getResults();
        if(results.size() >= 1)
            return new Location(results.get(0).getGeometry().getLocation());
        throw new LocationNotFoundException(place);
    }

    public static class LocationNotFoundException extends Exception {
        public LocationNotFoundException(String place) {
            super("Unable to found location of: "+place);
        }
    }
}
