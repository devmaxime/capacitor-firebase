package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.options;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import io.capawesome.capacitorjs.plugins.firebase.firestore.FirebaseFirestoreHelper;
import io.capawesome.capacitorjs.plugins.firebase.firestore.classes.constraints.QueryCompositeFilterConstraint;
import io.capawesome.capacitorjs.plugins.firebase.firestore.interfaces.QueryNonFilterConstraint;
import org.json.JSONException;

public class GetCollectionOptions {

    @NonNull
    private String reference;

    @Nullable
    private QueryCompositeFilterConstraint compositeFilter;

    @NonNull
    private QueryNonFilterConstraint[] queryConstraints;

    @Nullable
    private JSArray originalQueryConstraints;

    @Nullable
    private String databaseId;

    public GetCollectionOptions(
        String reference,
        @Nullable JSObject compositeFilter,
        @Nullable JSArray queryConstraints,
        @Nullable String databaseId
    ) throws JSONException {
        this.reference = reference;
        this.compositeFilter = FirebaseFirestoreHelper.createQueryCompositeFilterConstraintFromJSObject(compositeFilter);
        this.queryConstraints = FirebaseFirestoreHelper.createQueryNonFilterConstraintArrayFromJSArray(queryConstraints);
        this.originalQueryConstraints = queryConstraints;
        this.databaseId = databaseId;
    }

    @NonNull
    public String getReference() {
        return reference;
    }

    @Nullable
    public QueryCompositeFilterConstraint getCompositeFilter() {
        return compositeFilter;
    }

    @NonNull
    public QueryNonFilterConstraint[] getQueryConstraints() {
        return queryConstraints;
    }

    @Nullable
    public String getDatabaseId() {
        return databaseId;
    }

    @Nullable
    public JSArray getOriginalQueryConstraints() {
        return originalQueryConstraints;
    }
}
