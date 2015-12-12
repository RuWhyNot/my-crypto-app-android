package com.codextropy.mycryptoapp;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

	KeyStorage keyStorage;

	String currentKey;

	KeyStorage.Type activeKeyList;
	boolean isKeyListOpenedForChoice;

	ArrayList<DbKeyInfo> loadedKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

		keyStorage = new KeyStorage(getBaseContext());

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

        Button getPrivateBtn = (Button) findViewById(R.id.button3);
        getPrivateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OpenKeysLayout(true, KeyStorage.Type.Private);
			}
		});

        Button getPublicBtn = (Button) findViewById(R.id.button4);
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

		Button decryptBtn = (Button) findViewById(R.id.button2);
		decryptBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FullKeyInfo key = new FullKeyInfo();
				key.data = currentKey;
				EditText cipherField = (EditText) findViewById(R.id.editText2);
				String cipher = cipherField.getText().toString();
				String message = key.DecryptMessage(cipher);
				EditText resultField = (EditText) findViewById(R.id.editText3);
				resultField.setText(message);
			}
		});

		Button genNewBtn = (Button) findViewById(R.id.button13);
		genNewBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FullKeyInfo key = new FullKeyInfo();
				key.GeneratePrivate((int) Math.floor(Math.random() * 100000), 1024);
				FullKeyInfo pubKey = new FullKeyInfo();
				pubKey.GeneratePublic(key.data);
				keyStorage.SaveKey(key.data, KeyStorage.Type.Private);
				keyStorage.SaveKey(pubKey.data, KeyStorage.Type.Public);
				OpenKeysLayout(isKeyListOpenedForChoice, activeKeyList);
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
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, resultField.getText().toString());
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
			}
		});

		Button copyValue = (Button) findViewById(R.id.button7);
		copyValue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText valueField = (EditText) findViewById(R.id.editText2);
				ToClipboard(valueField.getText().toString());
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

		ListView privateKeysList = (ListView) findViewById(R.id.keysList);
		privateKeysList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FullKeyInfo key = keyStorage.GetKey(activeKeyList, loadedKeys.get(position).id);
				currentKey = key.data;
				TextView resultField = (TextView) findViewById(R.id.keyText);
				resultField.setText(currentKey);
				OpenEncryptionLayout();
			}
		});
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

	private void HideAllLayouts()
	{
		findViewById(R.id.encryptionLayout).setVisibility(View.GONE);
		findViewById(R.id.keysLayout).setVisibility(View.GONE);
	}

	private void FillKeysList(KeyStorage.Type type)
	{
		ListView keysList = (ListView) findViewById(R.id.keysList);

		ArrayList<DbKeyInfo> keys = keyStorage.GetAllKeys(type);
		ArrayList<String> helperList = new ArrayList<>();
		for (DbKeyInfo key : keys)
		{
			helperList.add(key.name);
		}

		String[] values = new String[helperList.size()];

		helperList.toArray(values);

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, values);
		keysList.setAdapter(adapter);

		activeKeyList = type;
		loadedKeys = keys;
	}

	private void OpenKeysLayout(boolean forChoice, KeyStorage.Type type)
	{
		isKeyListOpenedForChoice = forChoice;
		HideAllLayouts();
		findViewById(R.id.keysLayout).setVisibility(View.VISIBLE);

		if (type == KeyStorage.Type.Private)
		{
			findViewById(R.id.button13).setVisibility(View.VISIBLE);
		}
		else
		{
			findViewById(R.id.button13).setVisibility(View.GONE);
		}

		FillKeysList(type);
	}

	private void OpenEncryptionLayout()
	{
		HideAllLayouts();
		findViewById(R.id.encryptionLayout).setVisibility(View.VISIBLE);
	}

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_encrypt) {
			OpenEncryptionLayout();
        } else if (id == R.id.nav_keys) {
			OpenKeysLayout(false, KeyStorage.Type.Public);
        } else if (id == R.id.nav_slideshow) {
			HideAllLayouts();
        } else if (id == R.id.nav_manage) {
			HideAllLayouts();
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

    static
    {
        System.loadLibrary("CallNative");
    }
}
