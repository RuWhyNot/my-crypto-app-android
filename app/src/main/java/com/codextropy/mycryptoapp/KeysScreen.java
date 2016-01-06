package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

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

		UpdateKeysList(type);
	}

	@Override
	protected void InitListeners()
	{
		((ListView) screenView.findViewById(R.id.keysList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FullKeyInfo keyInfo = GetKey(position);
				screenManager.PushScreen(new KeyEditScreen(activity, keyInfo, keyStorage, systemInterface));
			}
		});

		((ListView) screenView.findViewById(R.id.keysList)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				PopupMenu popup = CreatePopupMenu(R.menu.context_actions_with_key, view);

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
							case R.id.action_remove:
								RemoveKey(position);
								return true;
							default:
								return false;
						}

					}
				});

				popup.show();
				return true;
			}
		});

		RegisterMenuItem(R.id.action_add, new MenuItemHandler() {
			@Override
			public void Handle() {
				if (type == KeyStorage.Type.Public) {
					CreateNewKey();
				} else {
					PopupMenu popup = CreatePopupMenu(R.menu.context_add_private_key, activity.findViewById(R.id.toolbar), Gravity.RIGHT);

					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getItemId()) {
								case R.id.action_generate_new:
									GenerateNewKey();
									return true;
								case R.id.action_add_existing:
									CreateNewKey();
									return true;
								default:
									return false;
							}

						}
					});

					popup.show();
				}
			}
		});
	}

	private void RemoveKey(int position) {
		int id = loadedKeys.get(position).id;
		keyStorage.RemoveKey(type, id);
		UpdateKeysList(type);
	}

	@NonNull
	private PopupMenu CreatePopupMenu(@MenuRes int id, View view) {
		return CreatePopupMenu(id, view, Gravity.NO_GRAVITY);
	}

	@NonNull
	private PopupMenu CreatePopupMenu(@MenuRes int id, View view, int gravity) {
		PopupMenu popup;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			popup = new PopupMenu(activity, view, gravity);
			popup.inflate(id);
		} else {
			popup = new PopupMenu(activity, view);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(id, popup.getMenu());
		}
		return popup;
	}

	private void GenerateNewKey() {
		FullKeyInfo key = new FullKeyInfo();
		key.GeneratePrivate((int) Math.floor(Math.random() * 100000), 1024);
		key.name = Integer.toHexString(key.fingerprint);
		key.keyType = type;
		screenManager.PushScreen(new KeyEditScreen(activity, key, keyStorage, systemInterface));
	}

	private void CreateNewKey() {
		FullKeyInfo keyInfo = new FullKeyInfo();
		keyInfo.keyType = type;
		screenManager.PushScreen(new KeyEditScreen(activity, keyInfo, keyStorage, systemInterface));
	}

	@Override
	protected void ClearListeners() {
		super.ClearListeners();

		((ListView) screenView.findViewById(R.id.keysList)).setOnItemClickListener(null);
	}

	private void UpdateKeysList(KeyStorage.Type type)
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
		int id = loadedKeys.get(position).id;
		FullKeyInfo keyInfo = keyStorage.GetKey(type, id);
		keyInfo.dbId = id;
		keyInfo.keyType = type;
		return keyInfo;
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
		UpdateKeysList(type);
		super.Show();
	}
}
