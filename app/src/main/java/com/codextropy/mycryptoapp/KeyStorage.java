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
			keyInfo.data = c.getString(c.getColumnIndex("data"));
			keyInfo.fingerprint = c.getInt(c.getColumnIndex("fingerprint"));
			keyInfo.name = c.getString(c.getColumnIndex("name"));
		}
		c.close();

		return keyInfo;
	}

	public void SaveKey(FullKeyInfo key, Type type)
	{
		DbHelper dbHelper = new DbHelper(baseContext);
		ContentValues cv = new ContentValues();

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);

		cv.put("data", key.data);
		cv.put("fingerprint", key.fingerprint);
		cv.put("name", key.name);
		long rowID = db.insert(tableName, null, cv);
		dbHelper.close();
	}

	public void RemoveKey(Type type, int id)
	{
		DbHelper dbHelper = new DbHelper(baseContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);
		db.delete(tableName, "id="+id, null);
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
			int idColName = c.getColumnIndex("name");
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
