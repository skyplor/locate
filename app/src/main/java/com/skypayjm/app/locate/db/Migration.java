package com.skypayjm.app.locate.db;

import com.skypayjm.app.locate.model.CategoryRelationship;

import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.internal.ColumnType;
import io.realm.internal.Table;

/**
 * Created by Sky on 15/7/2015.
 */
public class Migration implements RealmMigration {
    @Override
    public long execute(Realm realm, long version) {

        /*
            // Version 1
                class CategoryRelationship                   // add a new model class
                    String id;
                    Table childCategory;
                    String parentID;
                    Table parentCategory;
        */
        // Migrate from version 0 to version 1
        if (version == 0) {
            Table relationshipTable = realm.getTable(CategoryRelationship.class);
            relationshipTable.addColumn(ColumnType.STRING, "id");
            relationshipTable.addColumn(ColumnType.TABLE, "childCategory");
            relationshipTable.addColumn(ColumnType.STRING, "parentID");
            relationshipTable.addColumn(ColumnType.TABLE, "parentCategory");

            version++;
        }
        return version;
    }
}
