package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

public final class DecryptionScreen extends ScreenImpl {
	KeyStorage keyStorage;
	SystemInterface systemInterface;

	public DecryptionScreen(Activity activity, KeyStorage keyStorage, SystemInterface sysInterface) {
		super(activity, activity.findViewById(R.id.decryptionLayout));
		this.keyStorage = keyStorage;
		this.systemInterface = sysInterface;
	}

	@Override
	public String GetTitle() {
		return "Decryption";
	}

	@Override
	protected void InitListeners() {
		SetOnClickListener(R.id.decryptBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cipherField = (EditText) screenView.findViewById(R.id.cipherText);
				String cipher = cipherField.getText().toString();
				String message = keyStorage.DecryptMessage(cipher);
				EditText resultField = (EditText) screenView.findViewById(R.id.decryptedText);
				resultField.setText(message);
			}
		});

		SetOnClickListener(R.id.decryptedCopyBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText resultField = (EditText) screenView.findViewById(R.id.decryptedText);
				systemInterface.ToClipboard(resultField.getText().toString());
			}
		});

		SetOnClickListener(R.id.decryptedClearBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cipherField = (EditText) screenView.findViewById(R.id.decryptedText);
				cipherField.setText("");
			}
		});

		SetOnClickListener(R.id.cipherClearBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cipherField = (EditText) screenView.findViewById(R.id.cipherText);
				cipherField.setText("");
			}
		});

		SetOnClickListener(R.id.cipherPasteBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cipherField = (EditText) screenView.findViewById(R.id.cipherText);
				cipherField.setText(systemInterface.FromClipboard());
			}
		});
	}
}
