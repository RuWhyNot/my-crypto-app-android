package com.codextropy.mycryptoapp;

import android.support.annotation.Nullable;

import java.util.Set;
import java.util.Stack;

public final class ScreenManagerImpl implements ScreenManager {
	private Stack<Screen> screens;

	private Set<Integer> menuItemsToHide;
	private Set<Integer> menuItemsToShow;

	public ScreenManagerImpl() {
		this.screens = new Stack<>();
	}

	public void PushScreen(ScreenImpl screen) {
		screen.SetScreenManager(this);

		if (!screens.empty()) {
			screens.lastElement().Hide();
		}

		screens.push(screen);
		screen.Show();
	}

	@Override
	public Screen ReplaceCurrentScreen(ScreenImpl screen) {
		Screen lastScreen = PopScreen();
		PushScreen(screen);
		return lastScreen;
	}

	public Screen PopScreen() {
		if (!screens.empty()) {
			Screen last = screens.pop();
			last.Hide();
			last.Clear();

			if (!screens.empty()) {
				screens.lastElement().Show();
			}

			return last;
		} else {
			// error here
			return null;
		}
	}

	public void Clear() {
		if (!screens.empty()) {
			screens.lastElement().Hide();
		}

		while (!screens.empty()) {
			Screen last = screens.pop();
			last.Clear();
		}
	}

	@Nullable
	public Screen GetCurrent() {
		if (screens.empty()) {
			return null;
		}

		return screens.lastElement();
	}
}
