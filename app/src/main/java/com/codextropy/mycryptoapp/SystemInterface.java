package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

public final class SystemInterface {
	private Activity activity;

	public SystemInterface(Activity activity) {
		this.activity = activity;
	}

	public void ShareText(String text) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.setType("text/plain");
		activity.startActivity(sendIntent);
	}

	public void ToClipboard(String text)
	{
		ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("", text);
		clipboard.setPrimaryClip(clip);
	}

	@NonNull
	public String FromClipboard()
	{
		ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
		if (clipboard.getPrimaryClip().getItemCount() > 0)
		{
			return clipboard.getPrimaryClip().getItemAt(0).coerceToText(activity).toString();
		}
		else
		{
			return "";
		}
	}

	public void ShowMessage(String message, View view) {
		Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
	}
}
