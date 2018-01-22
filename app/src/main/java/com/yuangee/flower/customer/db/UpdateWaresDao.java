package com.yuangee.flower.customer.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.yuangee.flower.customer.entity.UpdateWares;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "UPDATE_WARES".
*/
public class UpdateWaresDao extends AbstractDao<UpdateWares, Long> {

    public static final String TABLENAME = "UPDATE_WARES";

    /**
     * Properties of entity UpdateWares.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Start = new Property(1, String.class, "start", false, "START");
        public final static Property End = new Property(2, String.class, "end", false, "END");
        public final static Property Value = new Property(3, boolean.class, "value", false, "VALUE");
    }


    public UpdateWaresDao(DaoConfig config) {
        super(config);
    }
    
    public UpdateWaresDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"UPDATE_WARES\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," + // 0: id
                "\"START\" TEXT," + // 1: start
                "\"END\" TEXT," + // 2: end
                "\"VALUE\" INTEGER NOT NULL );"); // 3: value
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"UPDATE_WARES\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UpdateWares entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String start = entity.getStart();
        if (start != null) {
            stmt.bindString(2, start);
        }
 
        String end = entity.getEnd();
        if (end != null) {
            stmt.bindString(3, end);
        }
        stmt.bindLong(4, entity.getValue() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UpdateWares entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String start = entity.getStart();
        if (start != null) {
            stmt.bindString(2, start);
        }
 
        String end = entity.getEnd();
        if (end != null) {
            stmt.bindString(3, end);
        }
        stmt.bindLong(4, entity.getValue() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public UpdateWares readEntity(Cursor cursor, int offset) {
        UpdateWares entity = new UpdateWares( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // start
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // end
            cursor.getShort(offset + 3) != 0 // value
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UpdateWares entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setStart(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setEnd(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setValue(cursor.getShort(offset + 3) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UpdateWares entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UpdateWares entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UpdateWares entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}