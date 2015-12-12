package com.codextropy.mycryptoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DbHelper extends SQLiteOpenHelper
{
	private static final String PRIVATE_KEYS_TABLE_NAME = "PrivateKeys";
	private static final String PUBLIC_KEYS_TABLE_NAME = "PublicKeys";

	public DbHelper(Context context)
    {
        super(context, "myDB", null, 1);
    }

	static public String GetKeysTableName(KeyStorage.Type type)
	{
		switch (type)
		{
			case Private:
				return PRIVATE_KEYS_TABLE_NAME;
			case Public:
				return PUBLIC_KEYS_TABLE_NAME;
			default:
				return "";
		}
	}

    @Override
    public void onCreate(SQLiteDatabase db)
    {
		db.execSQL("create table if not exists "+PRIVATE_KEYS_TABLE_NAME+" ("
				+ "id integer primary key autoincrement"
				+ ",fingerprint number"
				+ ",name text"
				+ ",data text" + ");");

		db.execSQL("create table if not exists "+PUBLIC_KEYS_TABLE_NAME+" ("
				+ "id integer primary key autoincrement"
				+ ",fingerprint number"
				+ ",name text"
				+ ",data text" + ");");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}
}
