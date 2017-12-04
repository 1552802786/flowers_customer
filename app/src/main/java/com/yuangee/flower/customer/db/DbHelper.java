package com.yuangee.flower.customer.db;

/**
 * Created by developerLzh on 2017/6/20 0020.
 */

import android.content.Context;

import com.yuangee.flower.customer.entity.Address;
import com.yuangee.flower.customer.entity.AuctionTime;
import com.yuangee.flower.customer.entity.BusinessTime;
import com.yuangee.flower.customer.entity.Coupon;
import com.yuangee.flower.customer.entity.DestineTime;
import com.yuangee.flower.customer.entity.Express;
import com.yuangee.flower.customer.entity.Genre;
import com.yuangee.flower.customer.entity.GenreSub;
import com.yuangee.flower.customer.entity.Member;
import com.yuangee.flower.customer.entity.MemberToken;
import com.yuangee.flower.customer.entity.Shop;
import com.yuangee.flower.customer.entity.UpdateWares;

import org.greenrobot.greendao.AbstractDao;

/**
 * @Description: 数据库操作类，由于greenDao的特殊性，不能在框架中搭建，
 * 所有数据库操作都可以参考该类实现自己的数据库操作管理类，不同的Dao实现
 * 对应的getAbstractDao方法就行。
 * @order: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:18.
 */
public class DbHelper {
    private static final String DB_NAME = "driver.db";//数据库名称
    private static DbHelper instance;
    private DaoMaster.DevOpenHelper mHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private static DBManager<Address, Long> addressLongDBManager;
    private static DBManager<AuctionTime, Long> auctionTimeLongDBManager;
    private static DBManager<BusinessTime, Long> businessTimeLongDBManager;
    private static DBManager<Coupon, Long> couponLongDBManager;
    private static DBManager<DestineTime, Long> destineTimeLongDBManager;
    private static DBManager<Express, Long> expressLongDBManager;
    private static DBManager<Member, Long> memberLongDBManager;
    private static DBManager<MemberToken, Long> memberTokenLongDBManager;
    private static DBManager<Shop, Long> shopLongDBManager;
    private static DBManager<UpdateWares, Long> updateWaresLongDBManager;

    private static DBManager<Genre, Long> genreLongDBManager;
    private static DBManager<GenreSub, Long> genreSubLongDBManager;

    private DbHelper() {

    }

    public static DbHelper getInstance() {
        if (instance == null) {
            synchronized (DbHelper.class) {
                if (instance == null) {
                    instance = new DbHelper();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public void init(Context context, String dbName) {
        mHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DBManager<Address, Long> getAddressLongDBManager() {
        if (addressLongDBManager == null) {
            addressLongDBManager = new DBManager<Address, Long>() {
                @Override
                public AbstractDao<Address, Long> getAbstractDao() {
                    return mDaoSession.getAddressDao();
                }
            };
        }
        return addressLongDBManager;
    }

    public DBManager<AuctionTime, Long> getAuctionTimeLongDBManager() {
        if (auctionTimeLongDBManager == null) {
            auctionTimeLongDBManager = new DBManager<AuctionTime, Long>() {
                @Override
                public AbstractDao<AuctionTime, Long> getAbstractDao() {
                    return mDaoSession.getAuctionTimeDao();
                }
            };
        }
        return auctionTimeLongDBManager;
    }

    public DBManager<BusinessTime, Long> getBusinessTimeLongDBManager() {
        if (businessTimeLongDBManager == null) {
            businessTimeLongDBManager = new DBManager<BusinessTime, Long>() {
                @Override
                public AbstractDao<BusinessTime, Long> getAbstractDao() {
                    return mDaoSession.getBusinessTimeDao();
                }
            };
        }
        return businessTimeLongDBManager;
    }

    public DBManager<Coupon, Long> getCouponLongDBManager() {
        if (couponLongDBManager == null) {
            couponLongDBManager = new DBManager<Coupon, Long>() {
                @Override
                public AbstractDao<Coupon, Long> getAbstractDao() {
                    return mDaoSession.getCouponDao();
                }
            };
        }
        return couponLongDBManager;
    }

    public DBManager<DestineTime, Long> getDestineTimeLongDBManager() {
        if (destineTimeLongDBManager == null) {
            destineTimeLongDBManager = new DBManager<DestineTime, Long>() {
                @Override
                public AbstractDao<DestineTime, Long> getAbstractDao() {
                    return mDaoSession.getDestineTimeDao();
                }
            };
        }
        return destineTimeLongDBManager;
    }

    public DBManager<Express, Long> getExpressLongDBManager() {
        if (expressLongDBManager == null) {
            expressLongDBManager = new DBManager<Express, Long>() {
                @Override
                public AbstractDao<Express, Long> getAbstractDao() {
                    return mDaoSession.getExpressDao();
                }
            };
        }
        return expressLongDBManager;
    }

    public DBManager<Member, Long> getMemberLongDBManager() {
        if (memberLongDBManager == null) {
            memberLongDBManager = new DBManager<Member, Long>() {
                @Override
                public AbstractDao<Member, Long> getAbstractDao() {
                    return mDaoSession.getMemberDao();
                }
            };
        }
        return memberLongDBManager;
    }

    public DBManager<MemberToken, Long> getMemberTokenLongDBManager() {
        if (memberTokenLongDBManager == null) {
            memberTokenLongDBManager = new DBManager<MemberToken, Long>() {
                @Override
                public AbstractDao<MemberToken, Long> getAbstractDao() {
                    return mDaoSession.getMemberTokenDao();
                }
            };
        }
        return memberTokenLongDBManager;
    }

    public DBManager<Shop, Long> getShopLongDBManager() {
        if (shopLongDBManager == null) {
            shopLongDBManager = new DBManager<Shop, Long>() {
                @Override
                public AbstractDao<Shop, Long> getAbstractDao() {
                    return mDaoSession.getShopDao();
                }
            };
        }
        return shopLongDBManager;
    }

    public DBManager<UpdateWares, Long> getUpdateWaresLongDBManager() {
        if (updateWaresLongDBManager == null) {
            updateWaresLongDBManager = new DBManager<UpdateWares, Long>() {
                @Override
                public AbstractDao<UpdateWares, Long> getAbstractDao() {
                    return mDaoSession.getUpdateWaresDao();
                }
            };
        }
        return updateWaresLongDBManager;
    }

    public  DBManager<Genre, Long> getGenreLongDBManager() {
        if (genreLongDBManager == null) {
            genreLongDBManager = new DBManager<Genre, Long>() {
                @Override
                public AbstractDao<Genre, Long> getAbstractDao() {
                    return mDaoSession.getGenreDao();
                }
            };
        }
        return genreLongDBManager;
    }

    public  DBManager<GenreSub, Long> getGenreSubLongDBManager() {
        if (genreSubLongDBManager == null) {
            genreSubLongDBManager = new DBManager<GenreSub, Long>() {
                @Override
                public AbstractDao<GenreSub, Long> getAbstractDao() {
                    return mDaoSession.getGenreSubDao();
                }
            };
        }
        return genreSubLongDBManager;
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public void clear() {
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

    public void close() {
        clear();
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }
}