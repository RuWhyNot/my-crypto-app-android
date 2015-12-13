package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

	KeyStorage keyStorage;

	String currentKey;

	KeyStorage.Type activeKeyList;
	boolean isKeyListOpenedForChoice;

	FullKeyInfo editableKey;

	List<DbKeyInfo> loadedKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		keyStorage = new KeyStorage(getBaseContext());

		OpenKeysLayout(false, KeyStorage.Type.Public); // default layout

		RegisterListeners();

		Intent receivedIntent = getIntent();
		String receivedAction = receivedIntent.getAction();
		if(receivedAction.equals(Intent.ACTION_SEND))
		{
			//String receivedType = receivedIntent.getType();
			DoShareInputAction(receivedIntent.getStringExtra(Intent.EXTRA_TEXT));
		}
	}

	private void RegisterListeners() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FullKeyInfo key = new FullKeyInfo();
				key.GeneratePrivate((int) Math.floor(Math.random() * 100000), 1024);

				FullKeyInfo pubKey = new FullKeyInfo();
				pubKey.GeneratePublic(key.data);

				String someMessage = GetTestString();

				String encrypted = pubKey.EncryptMessage(someMessage);
				String decrypted = key.DecryptMessage(encrypted);

				Snackbar.make(view, decrypted, Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		TextView getPublicBtn = (TextView) findViewById(R.id.keyText);
		getPublicBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OpenKeysLayout(true, KeyStorage.Type.Public);
			}
		});

		Button encryptBtn = (Button) findViewById(R.id.button);
		encryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullKeyInfo pubKey = new FullKeyInfo();
				pubKey.data = currentKey;
                EditText messageField = (EditText) findViewById(R.id.editText2);
				String message = messageField.getText().toString();
				String cipher = pubKey.EncryptMessage(message);
				EditText resultField = (EditText) findViewById(R.id.editText3);
                resultField.setText(cipher);
            }
        });

		Button decryptBtn = (Button) findViewById(R.id.button10);
		decryptBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cipherField = (EditText) findViewById(R.id.cipherText);
				String cipher = cipherField.getText().toString();
				String message = DecryptMessage(cipher);
				EditText resultField = (EditText) findViewById(R.id.decryptedText);
				resultField.setText(message);
			}
		});

		Button genNewBtn = (Button) findViewById(R.id.button13);
		genNewBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FullKeyInfo key = new FullKeyInfo();
				key.GeneratePrivate((int) Math.floor(Math.random() * 100000), 1024);
				key.name = Integer.toHexString(key.fingerprint);
				key.keyType = activeKeyList;
				OpenEditKeyLayout(key);
			}
		});

		Button copyRes = (Button) findViewById(R.id.button5);
		copyRes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText resultField = (EditText) findViewById(R.id.editText3);
				ToClipboard(resultField.getText().toString());
			}
		});

		Button shareBtn = (Button) findViewById(R.id.button6);
		shareBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText resultField = (EditText) findViewById(R.id.editText3);
				ShareText(resultField.getText().toString());
			}
		});

		Button clearRes = (Button) findViewById(R.id.button8);
		clearRes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText resField = (EditText) findViewById(R.id.editText3);
				resField.setText("");
			}
		});

		Button clearMess = (Button) findViewById(R.id.button12);
		clearMess.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText mesField = (EditText) findViewById(R.id.editText2);
				mesField.setText("");
			}
		});

		Button pasteMess = (Button) findViewById(R.id.button9);
		pasteMess.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText valueField = (EditText) findViewById(R.id.editText2);
				valueField.setText(FromClipboard());
			}
		});

		Button cipherPasteBtn = (Button) findViewById(R.id.cipherPasteBtn);
		cipherPasteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cipherField = (EditText) findViewById(R.id.cipherText);
				cipherField.setText(FromClipboard());
			}
		});

		Button cipherClearBtn = (Button) findViewById(R.id.cipherClearBtn);
		cipherClearBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cipherField = (EditText) findViewById(R.id.cipherText);
				cipherField.setText("");
			}
		});

		Button decryptedCopyBtn = (Button) findViewById(R.id.decryptedCopyBtn);
		decryptedCopyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText resultField = (EditText) findViewById(R.id.decryptedText);
				ToClipboard(resultField.getText().toString());
			}
		});

		Button decryptedClearBtn = (Button) findViewById(R.id.decryptedClearBtn);
		decryptedClearBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cipherField = (EditText) findViewById(R.id.decryptedText);
				cipherField.setText("");
			}
		});

		ListView privateKeysList = (ListView) findViewById(R.id.keysList);
		privateKeysList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!isKeyListOpenedForChoice) {
					view.performLongClick();
					return;
				}

				FullKeyInfo key = keyStorage.GetKey(activeKeyList, loadedKeys.get(position).id);
				currentKey = key.data;
				TextView resultField = (TextView) findViewById(R.id.keyText);
				resultField.setText(currentKey);
				OpenEncryptionLayout();
			}
		});

		privateKeysList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				FullKeyInfo keyInfo = keyStorage.GetKey(activeKeyList, loadedKeys.get(position).id);
				keyInfo.dbId = loadedKeys.get(position).id;
				keyInfo.keyType = activeKeyList;
				OpenEditKeyLayout(keyInfo);
				return true;
			}
		});

		Button addNewKeyBtn = (Button) findViewById(R.id.addNewKeyBtn);
		addNewKeyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FullKeyInfo keyInfo = new FullKeyInfo();
				keyInfo.keyType = activeKeyList;
				OpenEditKeyLayout(keyInfo);
			}
		});

		Button saveKeyBtn = (Button) findViewById(R.id.saveKeyBtn);
		saveKeyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = ((EditText) findViewById(R.id.keyNameText)).getText().toString();
				if (name.isEmpty()) {
					ShowMessage("You must specify a name", v);
					return;
				}

				FullKeyInfo key = new FullKeyInfo();

				key.data = ((EditText) findViewById(R.id.keyDataText)).getText().toString();
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

				OpenKeysLayout(isKeyListOpenedForChoice, editableKey.keyType);
				editableKey = null;
			}
		});

		Button deleteKeyBtn = (Button) findViewById(R.id.deleteKeyBtn);
		deleteKeyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editableKey.dbId != -1) {
					keyStorage.RemoveKey(activeKeyList, editableKey.dbId);
					OpenKeysLayout(isKeyListOpenedForChoice, activeKeyList);
				}
			}
		});

		Button genPubKeyBtn = (Button) findViewById(R.id.genPubKeyBtn);
		genPubKeyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = ((EditText) findViewById(R.id.keyNameText)).getText().toString();
				if (name.isEmpty()) {
					ShowMessage("You must specify a name", v);
					return;
				}

				FullKeyInfo key = new FullKeyInfo();
				key.name = name;
				key.data = ((EditText) findViewById(R.id.keyDataText)).getText().toString();
				GenNSavePubKey(key);
				ShowMessage("Public Key Is Generated", v);
			}
		});

		Button closeKeyEditBtn = (Button) findViewById(R.id.closeKeyEditBtn);
		closeKeyEditBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OpenKeysLayout(isKeyListOpenedForChoice, activeKeyList);
				editableKey = null;
			}
		});

		Button shareKeyBtn = (Button) findViewById(R.id.shareKeyBtn);
		shareKeyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareText(((EditText) findViewById(R.id.keyDataText)).getText().toString());
			}
		});

		Button sharePublicBtn = (Button) findViewById(R.id.sharePublicBtn);
		sharePublicBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FullKeyInfo key = new FullKeyInfo();
				key.GeneratePublic(((EditText) findViewById(R.id.keyDataText)).getText().toString());
				ShareText(key.data);
			}
		});

		Button showDecryptionDetailsBtn = (Button) findViewById(R.id.showDecryptionDetailsBtn);
		showDecryptionDetailsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View decryptionDetailsLayout = findViewById(R.id.decryptionDetailsLayout);
				if (decryptionDetailsLayout.getVisibility() == View.GONE) {
					decryptionDetailsLayout.setVisibility(View.VISIBLE);
					((Button)findViewById(R.id.showDecryptionDetailsBtn)).setText("Hide Details");
				} else {
					decryptionDetailsLayout.setVisibility(View.GONE);
					((Button)findViewById(R.id.showDecryptionDetailsBtn)).setText("Show Details");
				}
			}
		});


		Button copyEncryptedBtn = (Button) findViewById(R.id.copyEncryptedBtn);
		copyEncryptedBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = ((TextView) findViewById(R.id.shareResultView)).getText().toString();
				ToClipboard(result);
			}
		});
	}

	private void ShowMessage(String message, View view) {
		Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
	}

	private void DoShareInputAction(String data) {
		int dataType = GetDataType(data);
		// 0 = Not an application data
		// 1 = PlainText
		// 2 = PlainData
		// 3 = PublicKey
		// 4 = PrivateKey
		// 5 = Cipher
		// 6 = Signature

		if (dataType == 5) {
			OpenShareLayout();
			((TextView) findViewById(R.id.shareInputView)).setText(data);
			String decrypted = DecryptMessage(data);
			((TextView) findViewById(R.id.shareResultView)).setText(decrypted);
		}
		else if (dataType == 3) {
			FullKeyInfo keyInfo = new FullKeyInfo();
			keyInfo.keyType = KeyStorage.Type.Public;
			keyInfo.data = data;
			OpenEditKeyLayout(keyInfo);
		}
		else if (dataType == 0)
		{
			OpenEncryptionLayout();
			((EditText) findViewById(R.id.editText2)).setText(data);
		}
	}

	private void ShareText(String text) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	private void GenNSavePubKey(FullKeyInfo key) {
		String data = key.data;
		key.GeneratePublic(data);
		keyStorage.SaveKey(key, KeyStorage.Type.Public);
	}

	private void ToClipboard(String text)
	{
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("", text);
		clipboard.setPrimaryClip(clip);
	}

	private String FromClipboard()
	{
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if (clipboard.getPrimaryClip().getItemCount() > 0)
		{
			return clipboard.getPrimaryClip().getItemAt(0).coerceToText(this).toString();
		}
		else
		{
			return "";
		}
	}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	private String DecryptMessage(String cipher)
	{
		int fingerprint = GetDataFingerprint(cipher);
		List<FullKeyInfo> keys = keyStorage.GetKeysForFingerprint(KeyStorage.Type.Private, fingerprint);
		for (FullKeyInfo key : keys)
		{
			String message = key.DecryptMessage(cipher);
			if (!message.isEmpty()) {
				return message;
			}
		}

		return "";
	}

	private void HideAllLayouts()
	{
		findViewById(R.id.encryptionLayout).setVisibility(View.GONE);
		findViewById(R.id.decryptionLayout).setVisibility(View.GONE);
		findViewById(R.id.keysLayout).setVisibility(View.GONE);
		findViewById(R.id.editKeyLayout).setVisibility(View.GONE);
		findViewById(R.id.shareGetLayout).setVisibility(View.GONE);
	}

	private void FillKeysList(KeyStorage.Type type)
	{
		ListView keysList = (ListView) findViewById(R.id.keysList);

		List<DbKeyInfo> keys = keyStorage.GetAllKeys(type);
		List<String> helperList = new ArrayList<>();
		for (DbKeyInfo key : keys)
		{
			helperList.add(key.name);
		}

		String[] values = new String[helperList.size()];

		helperList.toArray(values);

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, values);
		keysList.setAdapter(adapter);

		loadedKeys = keys;
	}

	private void OpenKeysLayout(boolean forChoice, KeyStorage.Type type)
	{
		activeKeyList = type;
		isKeyListOpenedForChoice = forChoice;
		HideAllLayouts();
		findViewById(R.id.keysLayout).setVisibility(View.VISIBLE);

		if (type == KeyStorage.Type.Private)
		{
			this.setTitle("Private Keys");
			findViewById(R.id.button13).setVisibility(View.VISIBLE);
		}
		else
		{
			this.setTitle("Public Keys");
			findViewById(R.id.button13).setVisibility(View.GONE);
		}

		FillKeysList(type);
	}

	private void OpenEncryptionLayout() {
		HideAllLayouts();
		this.setTitle("Encryption");
		findViewById(R.id.encryptionLayout).setVisibility(View.VISIBLE);
	}

	private void OpenDecryptionLayout() {
		HideAllLayouts();
		this.setTitle("Decryption");
		findViewById(R.id.decryptionLayout).setVisibility(View.VISIBLE);
	}

	private void OpenEditKeyLayout(FullKeyInfo keyInfo) {
		HideAllLayouts();
		findViewById(R.id.editKeyLayout).setVisibility(View.VISIBLE);
		editableKey = keyInfo;

		String title;

		if (keyInfo.dbId != -1)
		{
			title = "Edit ";
			findViewById(R.id.deleteKeyBtn).setVisibility(View.VISIBLE);
		}
		else
		{
			title = "Create";
			findViewById(R.id.deleteKeyBtn).setVisibility(View.GONE);
		}

		((EditText) findViewById(R.id.keyNameText)).setText(keyInfo.name);
		((EditText) findViewById(R.id.keyDataText)).setText(keyInfo.data);

		if (keyInfo.keyType == KeyStorage.Type.Private)
		{
			title += "Private Key";
			findViewById(R.id.genPubKeyBtn).setVisibility(View.VISIBLE);
			findViewById(R.id.shareKeyBtn).setVisibility(View.GONE);
			findViewById(R.id.sharePublicBtn).setVisibility(View.VISIBLE);
		}
		else
		{
			title += "Public Key";
			findViewById(R.id.genPubKeyBtn).setVisibility(View.GONE);
			findViewById(R.id.shareKeyBtn).setVisibility(View.VISIBLE);
			findViewById(R.id.sharePublicBtn).setVisibility(View.GONE);
		}

		this.setTitle(title);
	}

	private void OpenShareLayout() {
		HideAllLayouts();
		this.setTitle(R.string.app_name);
		findViewById(R.id.shareGetLayout).setVisibility(View.VISIBLE);
	}

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_encrypt) {
			OpenEncryptionLayout();
		} else if (id == R.id.nav_decrypt) {
			OpenDecryptionLayout();
        } else if (id == R.id.nav_keys) {
			OpenKeysLayout(false, KeyStorage.Type.Private);
        } else if (id == R.id.nav_pub_keys) {
			OpenKeysLayout(false, KeyStorage.Type.Public);
        } else if (id == R.id.nav_share) {
			HideAllLayouts();
        } else if (id == R.id.nav_send) {
			HideAllLayouts();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public native String GetTestString();
	public native int GetDataFingerprint(String data);
	public native int GetDataType(String data);

    static
    {
        System.loadLibrary("CallNative");
    }
}
