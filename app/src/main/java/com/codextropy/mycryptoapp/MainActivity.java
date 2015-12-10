package com.codextropy.mycryptoapp;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

	KeyStorage keyStorage;

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
				String key = GeneratePrivateKey((int) Math.floor(Math.random() * 100000), 1024);
				String pubKey = GetPublicKey(key);

				String someMessage = GetTestString();

				String encrypted = EncryptMessage(someMessage, pubKey);
				String decrypted = DecryptMessage(encrypted, key);

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
				String key = keyStorage.GetFirstKey(KeyStorage.Type.Private);
				EditText resultField = (EditText) findViewById(R.id.editText);
				resultField.setText(key);
			}
		});

        Button getPublicBtn = (Button) findViewById(R.id.button4);
        getPublicBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pubKey = keyStorage.GetFirstKey(KeyStorage.Type.Public);
				EditText resultField = (EditText) findViewById(R.id.editText);
				resultField.setText(pubKey);
			}
		});

        Button encryptBtn = (Button) findViewById(R.id.button);
		encryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText keyField = (EditText) findViewById(R.id.editText);
                String pubKey = keyField.getText().toString();
                EditText messageField = (EditText) findViewById(R.id.editText2);
				String message = messageField.getText().toString();
				String cipher = EncryptMessage(message, pubKey);
				EditText resultField = (EditText) findViewById(R.id.editText3);
                resultField.setText(cipher);
            }
        });

		Button decryptBtn = (Button) findViewById(R.id.button2);
		decryptBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText keyField = (EditText) findViewById(R.id.editText);
				String key = keyField.getText().toString();
				EditText cipherField = (EditText) findViewById(R.id.editText2);
				String cipher = cipherField.getText().toString();
				String message = DecryptMessage(cipher, key);
				EditText resultField = (EditText) findViewById(R.id.editText3);
				resultField.setText(message);
			}
		});

		Button genNewBtn = (Button) findViewById(R.id.button13);
		genNewBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String key = GeneratePrivateKey((int) Math.floor(Math.random() * 100000), 1024);
				String pubKey = GetPublicKey(key);
				keyStorage.SaveKey(key, KeyStorage.Type.Private);
				keyStorage.SaveKey(pubKey, KeyStorage.Type.Public);
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

		Button copyKey = (Button) findViewById(R.id.button6);
		copyKey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText keyField = (EditText) findViewById(R.id.editText);
				ToClipboard(keyField.getText().toString());
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

		Button clearKey = (Button) findViewById(R.id.button11);
		clearKey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText keyField = (EditText) findViewById(R.id.editText);
				keyField.setText("");
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

		Button pasteKey = (Button) findViewById(R.id.button10);
		pasteKey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText keyField = (EditText) findViewById(R.id.editText);
				keyField.setText(FromClipboard());
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public native String GetTestString();
    public native String GeneratePrivateKey(int seed, int size);
	public native String GetPublicKey(String privateKey);
	public native String EncryptMessage(String message, String publicKey);
	public native String DecryptMessage(String cipher, String privateKey);

    static
    {
        System.loadLibrary("CallNative");
    }
}
