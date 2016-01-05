package com.codextropy.mycryptoapp;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public final class ShareGetScreen extends ScreenImpl {
	SystemInterface systemInterface;

	public ShareGetScreen(Activity activity, SystemInterface sysInterface) {
		super(activity, activity.findViewById(R.id.shareGetLayout));
		this.systemInterface = sysInterface;
	}

	@Override
	public String GetTitle() {
		return "Shared";
	}

	@Override
	protected void InitListeners() {
		SetOnClickListener(R.id.showDecryptionDetailsBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View decryptionDetailsLayout = screenView.findViewById(R.id.decryptionDetailsLayout);
				if (decryptionDetailsLayout.getVisibility() == View.GONE) {
					decryptionDetailsLayout.setVisibility(View.VISIBLE);
					((Button)screenView.findViewById(R.id.showDecryptionDetailsBtn)).setText("Hide Details");
				} else {
					decryptionDetailsLayout.setVisibility(View.GONE);
					((Button)screenView.findViewById(R.id.showDecryptionDetailsBtn)).setText("Show Details");
				}
			}
		});

		SetOnClickListener(R.id.copyEncryptedBtn, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = ((TextView) screenView.findViewById(R.id.shareResultView)).getText().toString();
				systemInterface.ToClipboard(result);
			}
		});
	}
}
