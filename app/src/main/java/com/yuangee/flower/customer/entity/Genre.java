package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.yuangee.flower.customer.db.DaoSession;
import com.yuangee.flower.customer.db.GenreSubDao;
import com.yuangee.flower.customer.db.GenreDao;

/**
 * Created by developerLzh on 2017/10/17 0017.
 * <p>
 * 大种类
 */
@Entity
public class Genre {
    
    @Id
    public long id;

    @Property
    public String genreName;//种类名
    @Property
    public int genreSequence;//排序

    @ToMany(referencedJoinProperty = "genreId")
    public List<GenreSub> genreSubs;

    @Property
    public boolean clicked;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1860928924)
    private transient GenreDao myDao;

    @Generated(hash = 496493217)
    public Genre(long id, String genreName, int genreSequence, boolean clicked) {
        this.id = id;
        this.genreName = genreName;
        this.genreSequence = genreSequence;
        this.clicked = clicked;
    }

    @Generated(hash = 235763487)
    public Genre() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGenreName() {
        return this.genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public int getGenreSequence() {
        return this.genreSequence;
    }

    public void setGenreSequence(int genreSequence) {
        this.genreSequence = genreSequence;
    }

    public boolean getClicked() {
        return this.clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 900594519)
    public List<GenreSub> getGenreSubs() {
        if (genreSubs == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GenreSubDao targetDao = daoSession.getGenreSubDao();
            List<GenreSub> genreSubsNew = targetDao._queryGenre_GenreSubs(id);
            synchronized (this) {
                if (genreSubs == null) {
                    genreSubs = genreSubsNew;
                }
            }
        }
        return genreSubs;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1545994043)
    public synchronized void resetGenreSubs() {
        genreSubs = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1923335069)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGenreDao() : null;
    }
}
