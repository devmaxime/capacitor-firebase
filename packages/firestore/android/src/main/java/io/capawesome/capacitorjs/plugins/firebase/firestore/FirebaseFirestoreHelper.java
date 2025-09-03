package io.capawesome.capacitorjs.plugins.firebase.firestore;

import static io.capawesome.capacitorjs.plugins.firebase.firestore.FirebaseFirestorePlugin.ERROR_CODE_PREFIX;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.getcapacitor.JSArray;
import com.google.firebase.firestore.Filter;
import com.getcapacitor.JSObject;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryCompositeFilterConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryEndAtConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryFieldFilterConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryLimitConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryOrderByConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryStartAtConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.interfaces.QueryNonFilterConstraint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    public static Filter createFilterFromWhereConstraints(@Nullable JSArray queryConstraints)
        throws JSONException {
        if (queryConstraints == null) {
            return null;
        }
        
        ArrayList<Filter> filters = new ArrayList<>();
        for (int i = 0; i < queryConstraints.length(); i++) {
            JSObject queryConstraint = JSObject.fromJSONObject(queryConstraints.getJSONObject(i));
            String queryConstraintType = queryConstraint.getString("type");
            if ("where".equals(queryConstraintType)) {
                QueryFieldFilterConstraint whereConstraint = new QueryFieldFilterConstraint(queryConstraint);
                Filter filter = whereConstraint.toFilter();
                if (filter != null) {
                    filters.add(filter);
                }
            }
        }
        
        if (filters.isEmpty()) {
            return null;
        } else if (filters.size() == 1) {
            return filters.get(0);
        } else {
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
