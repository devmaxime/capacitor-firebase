package io.capawesome.capacitorjs.plugins.firebase.firestore;

import static org.junit.Assert.*;

import com.google.firebase.Timestamp;
import org.junit.Test;

import java.util.Date;

/**
 * Test for FirebaseFirestoreHelper timestamp conversion functionality.
 */
public class FirebaseFirestoreHelperTest {

    @Test
    public void testConvertTimestampValue_ISOString() {
        // Test ISO 8601 string conversion
        String isoString = "2025-09-23T17:18:38.749Z";
        Object result = FirebaseFirestoreHelper.convertTimestampValue(isoString);
        
        assertTrue("ISO string should be converted to Timestamp", result instanceof Timestamp);
        
        // Test ISO string without milliseconds
        String isoStringNoMs = "2025-09-23T17:18:38Z";
        Object resultNoMs = FirebaseFirestoreHelper.convertTimestampValue(isoStringNoMs);
        assertTrue("ISO string without ms should be converted to Timestamp", resultNoMs instanceof Timestamp);
    }

    @Test
    public void testConvertTimestampValue_UnixTimestamp() {
        // Test Unix timestamp in milliseconds conversion
        Long unixTimestamp = 1758388718749L;
        Object result = FirebaseFirestoreHelper.convertTimestampValue(unixTimestamp);
        
        assertTrue("Unix timestamp should be converted to Timestamp", result instanceof Timestamp);
    }

    @Test
    public void testConvertTimestampValue_RegularValues() {
        // Test regular string (should not be converted)
        String regularString = "not_a_timestamp";
        Object result = FirebaseFirestoreHelper.convertTimestampValue(regularString);
        
        assertEquals("Regular string should not be converted", regularString, result);
        
        // Test regular number (should not be converted)
        Integer regularNumber = 42;
        Object numberResult = FirebaseFirestoreHelper.convertTimestampValue(regularNumber);
        assertEquals("Regular number should not be converted", regularNumber, numberResult);
        
        // Test null value
        Object nullResult = FirebaseFirestoreHelper.convertTimestampValue(null);
        assertNull("Null should remain null", nullResult);
    }

    @Test
    public void testConvertTimestampValue_InvalidTimestamps() {
        // Test number too small (not a valid timestamp)
        Integer tooSmall = 123;
        Object result = FirebaseFirestoreHelper.convertTimestampValue(tooSmall);
        assertEquals("Number too small should not be converted", tooSmall, result);
        
        // Test number too large (not a valid timestamp)
        Long tooLarge = 5000000000000L;
        Object largeResult = FirebaseFirestoreHelper.convertTimestampValue(tooLarge);
        assertEquals("Number too large should not be converted", tooLarge, largeResult);
        
        // Test invalid ISO string
        String invalidISO = "2025-13-40T25:70:70Z";
        Object invalidResult = FirebaseFirestoreHelper.convertTimestampValue(invalidISO);
        assertEquals("Invalid ISO string should not be converted", invalidISO, invalidResult);
    }
}