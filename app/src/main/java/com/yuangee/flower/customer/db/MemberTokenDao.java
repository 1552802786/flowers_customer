package com.yuangee.flower.customer.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.yuangee.flower.customer.entity.MemberToken;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MEMBER_TOKEN".
*/
public class MemberTokenDao extends AbstractDao<MemberToken, Long> {

    public static final String TABLENAME = "MEMBER_TOKEN";

    /**
     * Properties of entity MemberToken.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property MemberId = new Property(1, long.class, "memberId", false, "MEMBER_ID");
        public final static Property DeathDate = new Property(2, long.class, "deathDate", false, "DEATH_DATE");
        public final static Property Token = new Property(3, String.class, "token", false, "TOKEN");
    }


    public MemberTokenDao(DaoConfig config) {
        super(config);
    }
    
    public MemberTokenDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MEMBER_TOKEN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"MEMBER_ID\" INTEGER NOT NULL ," + // 1: memberId
                "\"DEATH_DATE\" INTEGER NOT NULL ," + // 2: deathDate
                "\"TOKEN\" TEXT);"); // 3: token
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MEMBER_TOKEN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MemberToken entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getMemberId());
        stmt.bindLong(3, entity.getDeathDate());
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(4, token);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MemberToken entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getMemberId());
        stmt.bindLong(3, entity.getDeathDate());
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(4, token);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public MemberToken readEntity(Cursor cursor, int offset) {
        MemberToken entity = new MemberToken( //
            cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // memberId
            cursor.getLong(offset + 2), // deathDate
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // token
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MemberToken entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setMemberId(cursor.getLong(offset + 1));
        entity.setDeathDate(cursor.getLong(offset + 2));
        entity.setToken(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MemberToken entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MemberToken entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MemberToken entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
