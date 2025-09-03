package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.options;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.google.firebase.firestore.Filter;
import io.capawesome.capacitorjs.plugins.firebase.firestore.FirebaseFirestoreHelper;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryCompositeFilterConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.interfaces.QueryNonFilterConstraint;
import org.json.JSONException;

public class AddCollectionSnapshotListenerOptions {

    @NonNull
    private String reference;

    @Nullable
    private QueryCompositeFilterConstraint compositeFilter;

    @Nullable
    private Filter whereFilter;

    @NonNull
    private QueryNonFilterConstraint[] queryConstraints;

    private String callbackId;

    private final boolean includeMetadataChanges;
    
    @Nullable
    private String databaseId;

    public AddCollectionSnapshotListenerOptions(
        String reference,
        @Nullable JSObject compositeFilter,
        @Nullable JSArray queryConstraints,
        @Nullable Boolean includeMetadataChanges,
        String callbackId,
        @Nullable String databaseId
    ) throws JSONException {
        this.reference = reference;
        this.compositeFilter = FirebaseFirestoreHelper.createQueryCompositeFilterConstraintFromJSObject(compositeFilter);
        this.whereFilter = FirebaseFirestoreHelper.createFilterFromWhereConstraints(queryConstraints);
        this.queryConstraints = FirebaseFirestoreHelper.createQueryNonFilterConstraintArrayFromJSArray(queryConstraints);
        this.includeMetadataChanges = includeMetadataChanges == null ? false : includeMetadataChanges;
        this.callbackId = callbackId;
        this.databaseId = databaseId;
    }

    public String getReference() {
        return reference;
    }

    @Nullable
    public QueryCompositeFilterConstraint getCompositeFilter() {
        return compositeFilter;
    }

    @Nullable
    public Filter getWhereFilter() {
        return whereFilter;
    }

    @NonNull
    public QueryNonFilterConstraint[] getQueryConstraints() {
        return queryConstraints;
    }

    public boolean isIncludeMetadataChanges() {
        return includeMetadataChanges;
    }

    public String getCallbackId() {
        return callbackId;
    }
    
    @Nullable
    public String getDatabaseId() {
        return databaseId;
    }
}
