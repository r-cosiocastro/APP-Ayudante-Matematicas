package com.rafaelcosio.mathhelper.database;

import com.rafaelcosio.mathhelper.models.User;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class UserDBH extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;
    private static UserDBH sInstance;

    static {
        CupboardFactory.setCupboard(new CupboardBuilder().useAnnotations().build());
        cupboard().register(User.class);
    }

    private UserDBH(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static synchronized UserDBH getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UserDBH(context);
        }
        return sInstance;
    }

    public static void addOrUpdate(Context context, User User) {
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        cupboard().withDatabase(db).put(User);
    }

    public static User getUser(Context context, String uid) {
        SQLiteDatabase db = getInstance(context).getReadableDatabase();
        return cupboard().withDatabase(db).query(User.class).withSelection( "uid = ?", uid).get();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
