package com.yuangee.flower.customer.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.yuangee.flower.customer.entity.Genre;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GENRE".
*/
public class GenreDao extends AbstractDao<Genre, Long> {

    public static final String TABLENAME = "GENRE";

    /**
     * Properties of entity Genre.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property GenreName = new Property(1, String.class, "genreName", false, "GENRE_NAME");
        public final static Property GenreSequence = new Property(2, int.class, "genreSequence", false, "GENRE_SEQUENCE");
        public final static Property Clicked = new Property(3, boolean.class, "clicked", false, "CLICKED");
    }

    private DaoSession daoSession;


    public GenreDao(DaoConfig config) {
        super(config);
    }
    
    public GenreDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GENRE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"GENRE_NAME\" TEXT," + // 1: genreName
                "\"GENRE_SEQUENCE\" INTEGER NOT NULL ," + // 2: genreSequence
                "\"CLICKED\" INTEGER NOT NULL );"); // 3: clicked
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GENRE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Genre entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String genreName = entity.getGenreName();
        if (genreName != null) {
            stmt.bindString(2, genreName);
        }
        stmt.bindLong(3, entity.getGenreSequence());
        stmt.bindLong(4, entity.getClicked() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Genre entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String genreName = entity.getGenreName();
        if (genreName != null) {
            stmt.bindString(2, genreName);
        }
        stmt.bindLong(3, entity.getGenreSequence());
        stmt.bindLong(4, entity.getClicked() ? 1L: 0L);
    }

    @Override
    protected final void attachEntity(Genre entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public Genre readEntity(Cursor cursor, int offset) {
        Genre entity = new Genre( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // genreName
            cursor.getInt(offset + 2), // genreSequence
            cursor.getShort(offset + 3) != 0 // clicked
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Genre entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setGenreName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setGenreSequence(cursor.getInt(offset + 2));
        entity.setClicked(cursor.getShort(offset + 3) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Genre entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Genre entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Genre entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
