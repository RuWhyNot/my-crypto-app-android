package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public final class KeyEditScreen extends ScreenImpl
{
	FullKeyInfo editableKey;
	KeyStorage keyStorage;
	SystemInterface systemInterface;

	public KeyEditScreen(Activity activity, FullKeyInfo editableKey, KeyStorage storage, SystemInterface sysInterface) {
		super(activity, activity.findViewById(R.id.editKeyLayout));
		this.editableKey = editableKey;
		this.keyStorage = storage;
		this.systemInterface = sysInterface;
	}

	@Override
	protected void InitListeners()
	{
		SetOnClickListener(R.id.saveKeyBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SaveKey();
				screenManager.PopScreen();
			}
		});

		SetOnClickListener(R.id.deleteKeyBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteKey();
				screenManager.PopScreen();
			}
		});

		SetOnClickListener(R.id.genPubKeyBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = ((EditText) screenView.findViewById(R.id.keyNameText)).getText().toString();
				if (name.isEmpty()) {
					systemInterface.ShowMessage("You must specify a name", v);
					return;
				}

				FullKeyInfo key = new FullKeyInfo();
				key.name = name;
				key.data = ((EditText) screenView.findViewById(R.id.keyDataText)).getText().toString();
				GenNSavePubKey(key);
				systemInterface.ShowMessage("Public Key Is Generated", v);
			}
		});

		SetOnClickListener(R.id.closeKeyEditBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				OpenKeysLayout(isKeyListOpenedForChoice, activeKeyList);
//				editableKey = null;
				screenManager.PopScreen();
			}
		});

		SetOnClickListener(R.id.shareKeyBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				systemInterface.ShareText(((EditText) screenView.findViewById(R.id.keyDataText)).getText().toString());
			}
		});

		SetOnClickListener(R.id.sharePublicBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FullKeyInfo key = new FullKeyInfo();
				key.GeneratePublic(((EditText) screenView.findViewById(R.id.keyDataText)).getText().toString());
				systemInterface.ShareText(key.data);
			}
		});

		SetOnClickListener(R.id.makeDefaultBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		SetOnClickListener(R.id.encryptForBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EncryptionScreen encryptionScreen = new EncryptionScreen(activity, systemInterface);
				screenManager.PushScreen(encryptionScreen);
				encryptionScreen.SetKey(editableKey);
			}
		});
	}

	@Override
	public void Show()
	{
		if (editableKey.dbId != -1)
		{
			screenView.findViewById(R.id.deleteKeyBtn).setVisibility(View.VISIBLE);
		}
		else
		{
			screenView.findViewById(R.id.deleteKeyBtn).setVisibility(View.GONE);
		}

		((EditText) screenView.findViewById(R.id.keyNameText)).setText(editableKey.name);
		((EditText) screenView.findViewById(R.id.keyDataText)).setText(editableKey.data);

		if (editableKey.keyType == KeyStorage.Type.Private)
		{
			screenView.findViewById(R.id.genPubKeyBtn).setVisibility(View.VISIBLE);
			screenView.findViewById(R.id.shareKeyBtn).setVisibility(View.GONE);
			screenView.findViewById(R.id.sharePublicBtn).setVisibility(View.VISIBLE);
			screenView.findViewById(R.id.encryptForBtn).setVisibility(View.GONE);
			screenView.findViewById(R.id.makeDefaultBtn).setVisibility(View.VISIBLE);
		}
		else
		{
			screenView.findViewById(R.id.genPubKeyBtn).setVisibility(View.GONE);
			screenView.findViewById(R.id.shareKeyBtn).setVisibility(View.VISIBLE);
			screenView.findViewById(R.id.sharePublicBtn).setVisibility(View.GONE);
			screenView.findViewById(R.id.encryptForBtn).setVisibility(View.VISIBLE);
			screenView.findViewById(R.id.makeDefaultBtn).setVisibility(View.GONE);
		}

		super.Show();
	}

	@Override
	public String GetTitle() {
		String title;

		if (editableKey.dbId != -1)
		{
			title = "Edit ";
		}
		else
		{
			title = "Create ";
		}

		if (editableKey.keyType == KeyStorage.Type.Private)
		{
			title += "Private Key";
		}
		else
		{
			title += "Public Key";
		}
		return title;
	}

	public void SaveKey()
	{
		String name = ((EditText) screenView.findViewById(R.id.keyNameText)).getText().toString();
		if (name.isEmpty()) {
			systemInterface.ShowMessage("You must specify a name", screenView);
			return;
		}

		FullKeyInfo key = new FullKeyInfo();

		key.data = ((EditText) screenView.findViewById(R.id.keyDataText)).getText().toString();
		key.name = name;
		key.UpdateFingerprint();

		if (editableKey.dbId != -1) {
			if (editableKey.keyType == KeyStorage.Type.Private) {
				keyStorage.UpdateKey(editableKey.dbId, key, KeyStorage.Type.Private);
			} else {
				keyStorage.UpdateKey(editableKey.dbId, key, KeyStorage.Type.Public);
			}
		}
		else {
			if (editableKey.keyType == KeyStorage.Type.Private) {
				keyStorage.SaveKey(key, KeyStorage.Type.Private);
			} else {
				keyStorage.SaveKey(key, KeyStorage.Type.Public);
			}
		}

		editableKey = null;
	}

	public void DeleteKey()
	{
		if (editableKey.dbId != -1) {
			keyStorage.RemoveKey(editableKey.keyType, editableKey.dbId);
		}
	}

	private void GenNSavePubKey(FullKeyInfo key) {
		String data = key.data;
		key.GeneratePublic(data);
		keyStorage.SaveKey(key, KeyStorage.Type.Public);
	}

	public FullKeyInfo GetKey()
	{
		return editableKey;
	}
}
