package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.options;

import androidx.annotation.Nullable;
import com.getcapacitor.JSObject;
import io.capawesome.capacitorjs.plugins.firebase.firestore.FirebaseFirestoreHelper;
import java.util.Map;
import org.json.JSONException;

public class SetDocumentOptions {

    private String reference;
    private Map<String, Object> data;
    private boolean merge;
    @Nullable
    private String databaseId;

    public SetDocumentOptions(String reference, JSObject data, boolean merge, @Nullable String databaseId) throws JSONException {
        this.reference = reference;
        this.data = FirebaseFirestoreHelper.createHashMapFromJSONObject(data);
        this.merge = merge;
        this.databaseId = databaseId;
    }

    public String getReference() {
        return reference;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public boolean getMerge() {
        return merge;
    }

    @Nullable
    public String getDatabaseId() {
        return databaseId;
    }
}
