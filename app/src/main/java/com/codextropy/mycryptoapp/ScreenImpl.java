package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

public abstract class ScreenImpl implements Screen
{
	abstract class MenuItemHandler {
		abstract public void Handle();
	}

	protected View screenView;
	protected Activity activity;
	private List<View> viewsToRemoveOnClickListeners;
	protected ScreenManagerImpl screenManager;
	private Map<Integer, MenuItemHandler> menuItems;

	public ScreenImpl(Activity activity, View view)
	{
		this.screenView = view;
		this.activity = activity;
		viewsToRemoveOnClickListeners = new ArrayList<>();
		menuItems = new Hashtable<>();
		InitListeners();
	}

	public final void SetScreenManager(ScreenManagerImpl screenManager) {
		this.screenManager = screenManager;
	}

	public void Clear() {
		ClearListeners();
	}

	public void Show()
	{
		screenView.setVisibility(View.VISIBLE);
		activity.setTitle(GetTitle());
		activity.invalidateOptionsMenu();
	}

	public final void Hide()
	{
		screenView.setVisibility(View.GONE);
	}

	abstract protected void InitListeners();

	protected void ClearListeners()
	{
		for (View view : viewsToRemoveOnClickListeners) {
			view.setOnClickListener(null);
		}

		viewsToRemoveOnClickListeners.clear();
	}

	protected final void SetOnClickListener(@IdRes int id, @NonNull View.OnClickListener listener) {
		View view = screenView.findViewById(id);
		view.setOnClickListener(listener);
		viewsToRemoveOnClickListeners.add(view);
	}

	protected final void RegisterMenuItem(@IdRes int id, @NonNull MenuItemHandler handler) {
		menuItems.put(id, handler);
	}

	@Override
	public final void HandleMenuItemPress(MenuItem item) {
		MenuItemHandler handler = menuItems.get(item.getItemId());
		if (handler != null) {
			handler.Handle();
		}
	}

	public final Set<Integer> GetRelatedMenuItems() {
		return menuItems.keySet();
	}
}
