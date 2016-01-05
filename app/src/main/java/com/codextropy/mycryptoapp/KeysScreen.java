package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public final class KeysScreen extends ScreenImpl
{
	private KeyStorage.Type type;
	List<DbKeyInfo> loadedKeys;
	KeyStorage keyStorage;
	SystemInterface systemInterface;

	public KeysScreen(Activity activity, KeyStorage.Type type, KeyStorage storage, SystemInterface sysInterface) {
		super(activity, activity.findViewById(R.id.keysLayout));
		this.type = type;
		this.keyStorage = storage;
		this.systemInterface = sysInterface;

		if (type == KeyStorage.Type.Private)
		{
			screenView.findViewById(R.id.button13).setVisibility(View.VISIBLE);
		}
		else
		{
			screenView.findViewById(R.id.button13).setVisibility(View.GONE);
		}

		FillKeysList(type);
	}

	@Override
	protected void InitListeners()
	{
		SetOnClickListener(R.id.button13, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FullKeyInfo key = new FullKeyInfo();
				key.GeneratePrivate((int) Math.floor(Math.random() * 100000), 1024);
				key.name = Integer.toHexString(key.fingerprint);
				key.keyType = type;
				screenManager.PushScreen(new KeyEditScreen(activity, key, keyStorage, systemInterface));
			}
		});

		SetOnClickListener(R.id.addNewKeyBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FullKeyInfo keyInfo = new FullKeyInfo();
				keyInfo.keyType = type;
				screenManager.PushScreen(new KeyEditScreen(activity, keyInfo, keyStorage, systemInterface));
			}
		});

		((ListView) screenView.findViewById(R.id.keysList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FullKeyInfo keyInfo = GetKey(position);
				keyInfo.dbId = loadedKeys.get(position).id;
				keyInfo.keyType = type;
				screenManager.PushScreen(new KeyEditScreen(activity, keyInfo, keyStorage, systemInterface));
			}
		});
	}

	@Override
	protected void ClearListeners() {
		super.ClearListeners();

		((ListView) screenView.findViewById(R.id.keysList)).setOnItemClickListener(null);
	}

	private void FillKeysList(KeyStorage.Type type)
	{
		ListView keysList = (ListView) screenView.findViewById(R.id.keysList);

		List<DbKeyInfo> keys = keyStorage.GetAllKeys(type);
		List<String> helperList = new ArrayList<>();
		for (DbKeyInfo key : keys)
		{
			helperList.add(key.name);
		}

		String[] values = new String[helperList.size()];

		helperList.toArray(values);

		ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
				android.R.layout.simple_list_item_1, values);
		keysList.setAdapter(adapter);

		loadedKeys = keys;
	}

	FullKeyInfo GetKey(int position)
	{
		return keyStorage.GetKey(type, loadedKeys.get(position).id);
	}

	@Override
	public String GetTitle() {
		if (type == KeyStorage.Type.Private) {
			return "My Private Keys";
		} else {
			return "Recipients";
		}
	}

	@Override
	public void Show() {
		FillKeysList(type);
		super.Show();
	}
}
