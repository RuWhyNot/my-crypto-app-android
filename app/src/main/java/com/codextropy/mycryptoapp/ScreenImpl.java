package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

public abstract class ScreenImpl implements Screen
{
	protected View screenView;
	protected Activity activity;
	private List<View> viewsToRemoveOnClickListeners;
	protected ScreenManagerImpl screenManager;

	public ScreenImpl(Activity activity, View view)
	{
		this.screenView = view;
		this.activity = activity;
		viewsToRemoveOnClickListeners = new ArrayList<>();
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
}
