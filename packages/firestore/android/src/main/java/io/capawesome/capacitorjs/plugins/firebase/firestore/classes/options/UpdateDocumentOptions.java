package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.options;

import androidx.annotation.Nullable;
import com.getcapacitor.JSObject;
import io.capawesome.capacitorjs.plugins.firebase.firestore.FirebaseFirestoreHelper;
import java.util.Map;
import org.json.JSONException;

public class UpdateDocumentOptions {

    private String reference;
    private Map<String, Object> data;
    @Nullable
    private String databaseId;

    public UpdateDocumentOptions(String reference, JSObject data, @Nullable String databaseId) throws JSONException {
        this.reference = reference;
        this.data = FirebaseFirestoreHelper.createHashMapFromJSONObject(data);
        this.databaseId = databaseId;
    }

    public String getReference() {
        return reference;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Nullable
    public String getDatabaseId() {
        return databaseId;
    }
}
