package com.codextropy.mycryptoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class KeyStorage {
	private Context baseContext;

	public enum Type
	{
		Public
		,Private
	}

	KeyStorage(Context context)
	{
		baseContext = context;
	}

	public FullKeyInfo GetKey(Type type, int id)
	{
		FullKeyInfo keyInfo = null;
		DbHelper dbHelper = new DbHelper(baseContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);

		Cursor c = db.query(tableName, null, "id="+id, null, null, null, null);

		if (c.moveToFirst())
		{
			keyInfo = new FullKeyInfo();
			keyInfo.data = c.getString(c.getColumnIndex("key"));
		}
		c.close();

		return keyInfo;
	}

	public void SaveKey(String key, Type type)
	{
		DbHelper dbHelper = new DbHelper(baseContext);
		ContentValues cv = new ContentValues();

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);

		cv.put("key", key);
		long rowID = db.insert(tableName, null, cv);
		dbHelper.close();
	}

	public ArrayList<DbKeyInfo> GetAllKeys(Type type)
	{
		DbHelper dbHelper = new DbHelper(baseContext);

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);
		Cursor c = db.query(tableName, null, null, null, null, null, null);

		ArrayList<DbKeyInfo> keys = new ArrayList<>();
		if (c.moveToFirst())
		{
			int idColName = c.getColumnIndex("key");
			int idColId = c.getColumnIndex("id");

			do
			{
				DbKeyInfo key = new DbKeyInfo();
				key.name = c.getString(idColName);
				key.id = c.getInt(idColId);
				keys.add(key);
			}
			while (c.moveToNext());
		}
		c.close();

		dbHelper.close();

		return keys;
	}
}
