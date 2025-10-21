package io.capawesome.capacitorjs.plugins.firebase.firestore;

import static org.junit.Assert.*;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

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

    @Test
    public void testConvertFirestoreValueForJS_NestedTimestamps() {
        // Create a nested structure with timestamps
        Map<String, Object> nestedObject = new HashMap<>();
        nestedObject.put("name", "test");

        // Create a timestamp for joined_at field
        Timestamp joinedAt = new Timestamp(new Date(1758388718749L));
        nestedObject.put("joined_at", joinedAt);

        Map<String, Object> member = new HashMap<>();
        member.put("name", "John Doe");
        member.put("joined_at", joinedAt);

        ArrayList<Object> members = new ArrayList<>();
        members.add(member);

        Map<String, Object> document = new HashMap<>();
        document.put("id", "test-doc");
        document.put("nested", nestedObject);
        document.put("members", members);

        // Test the conversion
        JSObject result = FirebaseFirestoreHelper.createJSObjectFromMap(document);

        // Verify the nested timestamp in the nested object
        JSObject nestedResult = JSObject.fromJSONObject((org.json.JSONObject) result.get("nested"));
        String nestedTimestamp = nestedResult.getString("joined_at");
        assertTrue("Nested timestamp should be converted to string", nestedTimestamp.startsWith("Timestamp("));

        // Verify the timestamp in the array
        JSArray membersArray = JSArray.fromJSONObject((org.json.JSONObject) result.get("members"));
        JSObject firstMember = JSObject.fromJSONObject(membersArray.getJSONObject(0));
        String memberTimestamp = firstMember.getString("joined_at");
        assertTrue("Array timestamp should be converted to string", memberTimestamp.startsWith("Timestamp("));

        // Verify other fields are preserved
        assertEquals("Nested object name should be preserved", "test", nestedResult.getString("name"));
        assertEquals("Member name should be preserved", "John Doe", firstMember.getString("name"));
    }

    @Test
    public void testConvertFirestoreValueForJS_TopLevelTimestamp() {
        // Test top-level timestamp conversion
        Timestamp timestamp = new Timestamp(new Date(1758388718749L));
        Map<String, Object> document = new HashMap<>();
        document.put("created_at", timestamp);

        JSObject result = FirebaseFirestoreHelper.createJSObjectFromMap(document);

        String timestampString = result.getString("created_at");
        assertTrue("Top-level timestamp should be converted to string", timestampString.startsWith("Timestamp("));
    }

    @Test
    public void testConvertFirestoreValueForJS_RegularValues() {
        // Test that regular values are preserved
        Map<String, Object> document = new HashMap<>();
        document.put("string_field", "test string");
        document.put("number_field", 42);
        document.put("boolean_field", true);

        JSObject result = FirebaseFirestoreHelper.createJSObjectFromMap(document);

        assertEquals("String field should be preserved", "test string", result.getString("string_field"));
        assertEquals("Number field should be preserved", 42, result.getInteger("number_field"));
        assertEquals("Boolean field should be preserved", true, result.getBool("boolean_field"));
    }
}
