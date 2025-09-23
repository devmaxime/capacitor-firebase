package io.capawesome.capacitorjs.plugins.firebase.firestore;

import static io.capawesome.capacitorjs.plugins.firebase.firestore.FirebaseFirestorePlugin.ERROR_CODE_PREFIX;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestoreException;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryCompositeFilterConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryEndAtConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryFieldFilterConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryLimitConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryOrderByConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryStartAtConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.interfaces.QueryNonFilterConstraint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseFirestoreHelper {

    public static HashMap<String, Object> createHashMapFromJSONObject(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = createObjectFromJSValue(object.get(key));
            map.put(key, value);
        }
        return map;
    }

    @Nullable
    public static JSObject createJSObjectFromMap(@Nullable Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        JSObject object = new JSObject();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof ArrayList) {
                value = createJSArrayFromArrayList((ArrayList) value);
            } else if (value instanceof Map) {
                value = createJSObjectFromMap((Map<String, Object>) value);
            }
            object.put(key, value);
        }
        return object;
    }

    public static Object createObjectFromJSValue(Object value) throws JSONException {
        if (value.toString().equals("null")) {
            return null;
        } else if (value instanceof JSONObject) {
            return createHashMapFromJSONObject((JSONObject) value);
        } else if (value instanceof JSONArray) {
            return createArrayListFromJSONArray((JSONArray) value);
        } else {
            return value;
        }
    }

    /**
     * Converts a JavaScript timestamp value to a Firestore Timestamp object if it appears to be a timestamp.
     * Handles ISO 8601 strings and Unix timestamps in milliseconds.
     *
     * @param value The JavaScript value that might be a timestamp
     * @return A Firestore Timestamp object if the value is a recognizable timestamp format, otherwise the original value
     */
    public static Object convertTimestampValue(Object value) {
        if (value == null) {
            return null;
        }

        // Handle ISO 8601 timestamp strings (e.g., "2025-09-23T17:18:38.749Z")
        if (value instanceof String) {
            String stringValue = (String) value;
            if (isISO8601Timestamp(stringValue)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = sdf.parse(stringValue);
                    return new Timestamp(date);
                } catch (ParseException e) {
                    // If parsing fails, try without milliseconds
                    try {
                        SimpleDateFormat sdfWithoutMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        sdfWithoutMs.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = sdfWithoutMs.parse(stringValue);
                        return new Timestamp(date);
                    } catch (ParseException e2) {
                        android.util.Log.w("FirebaseFirestoreHelper", "Failed to parse ISO 8601 timestamp: " + stringValue);
                        return value; // Return original value if parsing fails
                    }
                }
            }
        }

        // Handle Unix timestamp in milliseconds (e.g., 1758388718749)
        if (value instanceof Number) {
            long longValue = ((Number) value).longValue();
            // Check if this looks like a Unix timestamp in milliseconds
            // Reasonable range: between year 2000 (946684800000L) and year 2100 (4102444800000L)
            if (longValue >= 946684800000L && longValue <= 4102444800000L) {
                return new Timestamp(new Date(longValue));
            }
        }

        return value; // Return original value if not a timestamp
    }

    /**
     * Checks if a string matches the ISO 8601 timestamp format.
     */
    private static boolean isISO8601Timestamp(String value) {
        // Basic check for ISO 8601 format: YYYY-MM-DDTHH:mm:ss[.sss]Z
        return value != null && value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z");
    }

    @Nullable
    public static QueryCompositeFilterConstraint createQueryCompositeFilterConstraintFromJSObject(@Nullable JSObject compositeFilter)
        throws JSONException {
        if (compositeFilter == null) {
            return null;
        } else {
            return new QueryCompositeFilterConstraint(compositeFilter);
        }
    }

    @Nullable
    public static Filter createFilterFromWhereConstraints(@Nullable JSArray queryConstraints) throws JSONException {
        android.util.Log.d(
            "FirebaseFirestoreHelper",
            "createFilterFromWhereConstraints: called with queryConstraints=" +
            (queryConstraints != null ? queryConstraints.toString() : "null")
        );

        if (queryConstraints == null) {
            android.util.Log.d("FirebaseFirestoreHelper", "createFilterFromWhereConstraints: queryConstraints is null, returning null");
            return null;
        }

        ArrayList<Filter> filters = new ArrayList<>();
        for (int i = 0; i < queryConstraints.length(); i++) {
            JSObject queryConstraint = JSObject.fromJSONObject(queryConstraints.getJSONObject(i));
            String queryConstraintType = queryConstraint.getString("type");
            android.util.Log.d(
                "FirebaseFirestoreHelper",
                "createFilterFromWhereConstraints: constraint " + i + " type=" + queryConstraintType
            );

            if ("where".equals(queryConstraintType)) {
                android.util.Log.d(
                    "FirebaseFirestoreHelper",
                    "createFilterFromWhereConstraints: processing where constraint " + i + ": " + queryConstraint.toString()
                );
                QueryFieldFilterConstraint whereConstraint = new QueryFieldFilterConstraint(queryConstraint);
                Filter filter = whereConstraint.toFilter();
                if (filter != null) {
                    filters.add(filter);
                    android.util.Log.d("FirebaseFirestoreHelper", "createFilterFromWhereConstraints: added filter for constraint " + i);
                } else {
                    android.util.Log.w(
                        "FirebaseFirestoreHelper",
                        "createFilterFromWhereConstraints: toFilter() returned null for constraint " + i
                    );
                }
            }
        }

        android.util.Log.d("FirebaseFirestoreHelper", "createFilterFromWhereConstraints: found " + filters.size() + " where filters");

        if (filters.isEmpty()) {
            android.util.Log.d("FirebaseFirestoreHelper", "createFilterFromWhereConstraints: no filters found, returning null");
            return null;
        } else if (filters.size() == 1) {
            android.util.Log.d("FirebaseFirestoreHelper", "createFilterFromWhereConstraints: returning single filter");
            return filters.get(0);
        } else {
            android.util.Log.d(
                "FirebaseFirestoreHelper",
                "createFilterFromWhereConstraints: returning combined filter with " + filters.size() + " filters"
            );
            return Filter.and(filters.toArray(new Filter[0]));
        }
    }

    @NonNull
    public static QueryNonFilterConstraint[] createQueryNonFilterConstraintArrayFromJSArray(@Nullable JSArray queryConstraints)
        throws JSONException {
        if (queryConstraints == null) {
            return new QueryNonFilterConstraint[0];
        } else {
            // Use ArrayList to filter out unsupported constraint types
            ArrayList<QueryNonFilterConstraint> validConstraints = new ArrayList<>();
            for (int i = 0; i < queryConstraints.length(); i++) {
                JSObject queryConstraint = JSObject.fromJSONObject(queryConstraints.getJSONObject(i));
                String queryConstraintType = queryConstraint.getString("type");
                QueryNonFilterConstraint constraint = null;
                switch (queryConstraintType) {
                    case "orderBy":
                        constraint = new QueryOrderByConstraint(queryConstraint);
                        break;
                    case "limit":
                    case "limitToLast":
                        constraint = new QueryLimitConstraint(queryConstraint);
                        break;
                    case "startAt":
                    case "startAfter":
                        constraint = new QueryStartAtConstraint(queryConstraint);
                        break;
                    case "endAt":
                    case "endBefore":
                        constraint = new QueryEndAtConstraint(queryConstraint);
                        break;
                    case "where":
                        // Skip where constraints - they should be handled as filter constraints
                        // This prevents null entries and allows them to be processed separately
                        break;
                    default:
                        // Skip unsupported constraint types instead of creating null entries
                        break;
                }
                if (constraint != null) {
                    validConstraints.add(constraint);
                }
            }
            return validConstraints.toArray(new QueryNonFilterConstraint[0]);
        }
    }

    private static ArrayList<Object> createArrayListFromJSONArray(JSONArray array) throws JSONException {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            Object value = array.get(x);
            if (value instanceof JSONObject) {
                value = createHashMapFromJSONObject((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = createArrayListFromJSONArray((JSONArray) value);
            }
            arrayList.add(value);
        }
        return arrayList;
    }

    private static JSArray createJSArrayFromArrayList(ArrayList arrayList) {
        JSArray array = new JSArray();
        for (Object value : arrayList) {
            if (value instanceof Map) {
                value = createJSObjectFromMap((Map<String, Object>) value);
            }
            array.put(value);
        }
        return array;
    }

    public static JSObject createSnapshotMetadataResult(DocumentSnapshot snapshot) {
        final JSObject obj = new JSObject();
        obj.put("fromCache", snapshot.getMetadata().isFromCache());
        obj.put("hasPendingWrites", snapshot.getMetadata().hasPendingWrites());
        return obj;
    }

    @Nullable
    public static String createErrorCode(@Nullable Exception exception) {
        if (exception == null) {
            return null;
        } else if (exception instanceof FirebaseFirestoreException) {
            String errorCode = ((FirebaseFirestoreException) exception).getCode().name();
            String prefixedErrorCode = String.format("%s/%s", ERROR_CODE_PREFIX, errorCode);
            return snakeToKebabCase(prefixedErrorCode);
        }
        return null;
    }

    private static String snakeToKebabCase(String snakeCase) {
        return snakeCase.replaceAll("_+", "-").toLowerCase();
    }
}
