package com.yuangee.flower.customer.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.FlowerApp;

import org.greenrobot.greendao.database.Database;

import static com.yuangee.flower.customer.db.DaoMaster.dropAllTables;

/**
 * Created by liuzihao on 2017/12/18.
 */

public class CusDevOpenHelper extends DaoMaster.OpenHelper {
    public CusDevOpenHelper(Context context, String name) {
        super(context, name);
    }

    public CusDevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
        dropAllTables(db, true);
        onCreate(db);

        SharedPreferences.Editor editor = App.me().getSharedPreferences().edit();
        editor.clear().apply();
    }
}
