package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.getcapacitor.JSObject;
import com.google.firebase.firestore.Filter;
import io.capawesome.capacitorjs.plugins.firebase.firestore.FirebaseFirestoreHelper;
import io.capawesome.capacitorjs.plugins.firebase.firestore.interfaces.QueryFilterConstraint;
import java.util.List;
import org.json.JSONException;

public class QueryFieldFilterConstraint implements QueryFilterConstraint {

    @NonNull
    private String fieldPath;

    @NonNull
    private String opStr;

    @NonNull
    private Object value;

    public QueryFieldFilterConstraint(JSObject queryConstraint) throws JSONException {
        this.fieldPath = queryConstraint.getString("fieldPath", "");
        this.opStr = queryConstraint.getString("opStr", "");
        this.value = FirebaseFirestoreHelper.createObjectFromJSValue(queryConstraint.get("value"));

        android.util.Log.d("QueryFieldFilterConstraint", "Constructor: fieldPath=" + fieldPath + ", opStr=" + opStr + ", value=" + value);
    }

    @NonNull
    public String getFieldPath() {
        return fieldPath;
    }

    @NonNull
    public String getOpStr() {
        return opStr;
    }

    @NonNull
    public Object getValue() {
        return value;
    }

    @Nullable
    public Filter toFilter() {
        android.util.Log.d("QueryFieldFilterConstraint", "toFilter: fieldPath=" + fieldPath + ", opStr=" + opStr + ", value=" + value);

        switch (opStr) {
            case "<":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating lessThan filter");
                return Filter.lessThan(fieldPath, value);
            case "<=":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating lessThanOrEqualTo filter");
                return Filter.lessThanOrEqualTo(fieldPath, value);
            case "==":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating equalTo filter");
                return Filter.equalTo(fieldPath, value);
            case ">=":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating greaterThanOrEqualTo filter");
                return Filter.greaterThanOrEqualTo(fieldPath, value);
            case ">":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating greaterThan filter");
                return Filter.greaterThan(fieldPath, value);
            case "!=":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating notEqualTo filter");
                return Filter.notEqualTo(fieldPath, value);
            case "array-contains":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating arrayContains filter");
                return Filter.arrayContains(fieldPath, value);
            case "array-contains-any":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating arrayContainsAny filter");
                return Filter.arrayContainsAny(fieldPath, (List<? extends Object>) value);
            case "in":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating inArray filter");
                return Filter.inArray(fieldPath, (List<? extends Object>) value);
            case "not-in":
                android.util.Log.d("QueryFieldFilterConstraint", "toFilter: creating notInArray filter");
                return Filter.notInArray(fieldPath, (List<? extends Object>) value);
            default:
                android.util.Log.w("QueryFieldFilterConstraint", "toFilter: unknown opStr '" + opStr + "', returning null");
                return null;
        }
    }
}
