package com.codextropy.mycryptoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class KeyStorage {
	Context BaseContext;

	public enum Type
	{
		Public
		,Private
	}

	KeyStorage(Context context)
	{
		BaseContext = context;
	}

	public String GetFirstKey(Type type)
	{
		DbHelper dbHelper = new DbHelper(BaseContext);
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
		DbHelper dbHelper = new DbHelper(BaseContext);
		ContentValues cv = new ContentValues();

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);
		//db.delete("RsaBdKeys", null, null);

		cv.put("key", key);
		long rowID = db.insert(tableName, null, cv);
		dbHelper.close();
	}
}
