package com.codextropy.mycryptoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public final class KeyStorage {
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

	public native int GetDataFingerprint(String data);

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

	public List<FullKeyInfo> GetKeysForFingerprint(Type type, int fingerprint)
	{
		List<FullKeyInfo> result = new ArrayList<>();

		DbHelper dbHelper = new DbHelper(baseContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);

		Cursor c = db.query(tableName, null, "fingerprint=" + fingerprint, null, null, null, null);

		if (c.moveToFirst())
		{
			int idColData = c.getColumnIndex("data");
			int idColFingerprint = c.getColumnIndex("fingerprint");
			int idColName = c.getColumnIndex("name");

			do
			{
				FullKeyInfo key = new FullKeyInfo();
				key.data = c.getString(idColData);
				key.fingerprint = c.getInt(idColFingerprint);
				key.name = c.getString(idColName);
				result.add(key);
			}
			while (c.moveToNext());
		}
		c.close();

		return result;
	}

	public int SaveKey(FullKeyInfo key, Type type)
	{
		DbHelper dbHelper = new DbHelper(baseContext);
		ContentValues cv = new ContentValues();

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);

		cv.put("data", key.data);
		cv.put("fingerprint", key.fingerprint);
		cv.put("name", key.name);
		long id = db.insert(tableName, null, cv);
		dbHelper.close();
		return (int)id;
	}

	public void UpdateKey(int id, FullKeyInfo key, Type type)
	{
		RemoveKey(type, id);
		SaveKey(key, type);
	}

	public void RemoveKey(Type type, int id)
	{
		DbHelper dbHelper = new DbHelper(baseContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = DbHelper.GetKeysTableName(type);
		db.delete(tableName, "id=" + id, null);
	}

	public List<DbKeyInfo> GetAllKeys(Type type)
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

	public String DecryptMessage(String cipher)
	{
		int fingerprint = GetDataFingerprint(cipher);
		List<FullKeyInfo> keys = GetKeysForFingerprint(KeyStorage.Type.Private, fingerprint);
		for (FullKeyInfo key : keys)
		{
			String message = key.DecryptMessage(cipher);
			if (!message.isEmpty()) {
				return message;
			}
		}

		return "";
	}
}
