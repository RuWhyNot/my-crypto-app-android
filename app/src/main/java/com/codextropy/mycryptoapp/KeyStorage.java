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

	public String GetFirstKey(Type type)
	{
		DbHelper dbHelper = new DbHelper(baseContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);

		Cursor c = db.query(tableName, null, null, null, null, null, null);

		String key = "";
		if (c.moveToFirst())
		{
			// определяем номера столбцов по имени в выборке
			int publicKeyColIndex = c.getColumnIndex("key");
			key = c.getString(publicKeyColIndex);
		}
		c.close();

		return key;
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

	public ArrayList<KeyInfo> GetAllKeys(Type type)
	{
		DbHelper dbHelper = new DbHelper(baseContext);

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);
		Cursor c = db.query(tableName, null, null, null, null, null, null);

		ArrayList<KeyInfo> keys = new ArrayList<>();
		if (c.moveToFirst())
		{
			int idColKey = c.getColumnIndex("key");

			do
			{
				KeyInfo key = new KeyInfo();
				key.data = c.getString(idColKey);
				keys.add(key);
			}
			while (c.moveToNext());
		}
		c.close();

		dbHelper.close();

		return keys;
	}
}
