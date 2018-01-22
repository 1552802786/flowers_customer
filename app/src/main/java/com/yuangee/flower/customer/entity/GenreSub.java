package com.yuangee.flower.customer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by developerLzh on 2017/10/17 0017.
 * <p>
 * 小种类
 */
@Entity
public class GenreSub {

    @Id
    public long id;

    @Property
    public long genreId;
    @Property
    public String genreName;

    @Property
    public String name;
    public String depictName;
    @Transient
    public boolean clicked;

    @Generated(hash = 588478636)
    public GenreSub(long id, long genreId, String genreName, String name,
            String depictName) {
        this.id = id;
        this.genreId = genreId;
        this.genreName = genreName;
        this.name = name;
        this.depictName = depictName;
    }

    @Generated(hash = 773338725)
    public GenreSub() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGenreId() {
        return this.genreId;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return this.genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepictName() {
        return this.depictName;
    }

    public void setDepictName(String depictName) {
        this.depictName = depictName;
    }
}
