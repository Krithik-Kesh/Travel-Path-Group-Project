package GeolocationsAPIs;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 Class for User cleaning up the user input for their location
 so it can be called by the API
 */
public class UserLocation {

    private static final String StreetNumber= "24 ";

    //USERS UNFORMATTED LOCATION
    public String rawLocation;

    //THE CLEANED UP STRING READY FOR THE API
    public String encodedLocation;

    public UserLocation(String rawLocation) {
        this.rawLocation = rawLocation;

        //WHEN USER LOCATION IS MADE, IMMEDIATELY CALL THE ENCODED LOCATION TO GET THE PROPER LOCATION
        this.encodedLocation = formatAndEncodeAddress(rawLocation);
    }

    //HELPER METHODS FOR CLEANUP
    private String formatAndEncodeAddress(String rawAddress) {
        if (rawAddress == null || rawAddress.trim().isEmpty()) {
            return "";
        }

        String cleanedAddress = rawAddress.trim().toLowerCase();

        //HANDLE THE CASE WITH PLUS IN THE ADDRESS
        if (cleanedAddress.contains("+")) {
            return formatPlusCode(cleanedAddress);
        }

        // ADD THE STREET NUMBER TO THE CLEANED ADDRESS
        if (!Character.isDigit(cleanedAddress.charAt(0))) {
            cleanedAddress = StreetNumber + cleanedAddress;
        }

        // REPLACE THE COMMON NAMES OF LOCATIONS
        cleanedAddress = cleanedAddress.replaceAll("ontario", "ON")
                .replaceAll("quebec", "QC")
                .replaceAll("alberta", "AB")
                .replaceAll("british columbia", "BC");

        //URL ENCODER TURNS THE SPACES INTO %20 OR '+'
        String encoder = URLEncoder.encode(cleanedAddress, StandardCharsets.UTF_8);

        return encoder;
    }

    private String formatPlusCode(String rawCode) {
        String encoded = rawCode;

        encoded = URLEncoder.encode(encoded, StandardCharsets.UTF_8);

        //REPLACE THE PLUS SIGN WITH THE PERCENT 2B
        encoded = encoded.replaceAll("\\+", "%2B");

        return encoded;
    }
}