package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public final class EncryptionScreen extends ScreenImpl {
	FullKeyInfo key;
	SystemInterface systemInterface;

	public EncryptionScreen(Activity activity, SystemInterface sysInterface) {
		super(activity, activity.findViewById(R.id.encryptionLayout));
		this.systemInterface = sysInterface;
	}

	public void SetKey(FullKeyInfo key)
	{
		this.key = key;
		TextView keyText = (TextView) screenView.findViewById(R.id.keyText);
		keyText.setText(key.name);
	}

	public void SetText(String text) {
		((EditText) screenView.findViewById(R.id.editText2)).setText(text);
	}

	public void Encrypt() {
		if (key == null) {
			return;
		}

		EditText messageField = (EditText) screenView.findViewById(R.id.editText2);
		String message = messageField.getText().toString();
		String cipher = key.EncryptMessage(message);
		EditText resultField = (EditText) screenView.findViewById(R.id.editText3);
		resultField.setText(cipher);
	}

	@Override
	public String GetTitle() {
		return "Encryption";
	}

	@Override
	protected void InitListeners() {
		SetOnClickListener(R.id.button, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Encrypt();
			}
		});

		SetOnClickListener(R.id.button5, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText resultField = (EditText) screenView.findViewById(R.id.editText3);
				systemInterface.ToClipboard(resultField.getText().toString());
			}
		});

		SetOnClickListener(R.id.button9, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText valueField = (EditText) screenView.findViewById(R.id.editText2);
				valueField.setText(systemInterface.FromClipboard());
			}
		});

		SetOnClickListener(R.id.button12, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText mesField = (EditText) screenView.findViewById(R.id.editText2);
				mesField.setText("");
			}
		});

		SetOnClickListener(R.id.button8, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText resField = (EditText) screenView.findViewById(R.id.editText3);
				resField.setText("");
			}
		});

		SetOnClickListener(R.id.button6, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText resultField = (EditText) screenView.findViewById(R.id.editText3);
				systemInterface.ShareText(resultField.getText().toString());
			}
		});
	}
}
