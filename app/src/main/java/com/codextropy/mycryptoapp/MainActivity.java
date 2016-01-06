package com.codextropy.mycryptoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Set;

public final class MainActivity extends AppCompatActivity {

	private KeyStorage keyStorage;
	private ScreenManager screenManager;
	private SystemInterface systemInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		keyStorage = new KeyStorage(getBaseContext());
		screenManager = new ScreenManagerImpl();
		systemInterface = new SystemInterface(this);

		OpenKeysLayout(KeyStorage.Type.Public); // open default layout

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
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem item) {
				int id = item.getItemId();

				screenManager.Clear();
				if (id == R.id.nav_encrypt) {
					OpenEncryptionLayout();
				} else if (id == R.id.nav_decrypt) {
					OpenDecryptionLayout();
				} else if (id == R.id.nav_keys) {
					OpenKeysLayout(KeyStorage.Type.Private);
				} else if (id == R.id.nav_recipients) {
					OpenKeysLayout(KeyStorage.Type.Public);
				} else if (id == R.id.nav_share) {
					//
				} else if (id == R.id.nav_send) {
					//
				}

				DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
				drawer.closeDrawer(GravityCompat.START);
				return true;
			}
		});
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
			String decrypted = keyStorage.DecryptMessage(data);
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

    @Override
    public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
			return;
		}

		screenManager.PopScreen();

		if (screenManager.GetCurrent() == null) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

		Screen currentScreen = screenManager.GetCurrent();

		if (currentScreen != null) {
			Set<Integer> menuItemsToShow = currentScreen.GetRelatedMenuItems();

			for (int i = 0; i < menu.size(); i++) {
				MenuItem item = menu.getItem(i);
				item.setVisible(menuItemsToShow.contains(menu.getItem(i).getItemId()));
			}
		}

        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Screen currentScreen = screenManager.GetCurrent();

		if (currentScreen != null) {
			currentScreen.HandleMenuItemPress(item);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void OpenKeysLayout(KeyStorage.Type type)
	{
		screenManager.PushScreen(new KeysScreen(this, type, keyStorage, systemInterface));
	}

	private void OpenEncryptionLayout() {
		screenManager.PushScreen(new EncryptionScreen(this, systemInterface));
	}

	private void OpenDecryptionLayout() {
		screenManager.PushScreen(new DecryptionScreen(this, keyStorage, systemInterface));
	}

	private void OpenEditKeyLayout(FullKeyInfo keyInfo) {
		screenManager.PushScreen(new KeyEditScreen(this, keyInfo, keyStorage, systemInterface));
	}

	private void OpenShareLayout() {
		screenManager.PushScreen(new ShareGetScreen(this, systemInterface));
	}

    public native String GetTestString();
	public native int GetDataType(String data);

    static
    {
        System.loadLibrary("CallNative");
    }
}
