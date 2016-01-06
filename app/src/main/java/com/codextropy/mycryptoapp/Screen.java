package com.codextropy.mycryptoapp;

import android.view.MenuItem;

import java.util.Set;

public interface Screen {
	void Clear();
	void Show();
	void Hide();
	String GetTitle();

	void HandleMenuItemPress(MenuItem item);
	Set<Integer> GetRelatedMenuItems();
}
