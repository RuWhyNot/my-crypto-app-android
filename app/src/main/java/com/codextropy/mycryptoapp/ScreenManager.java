package com.codextropy.mycryptoapp;

import android.support.annotation.Nullable;

public interface ScreenManager {
	void PushScreen(ScreenImpl screen);
	Screen ReplaceCurrentScreen(ScreenImpl screen);
	Screen PopScreen();
	void Clear();

	@Nullable
	Screen GetCurrent();
}
