package com.roberteves.heobserver.core;

import com.roberteves.heobserver.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;

public class Dialogs {
	public static final int TYPE_INFO = 0, TYPE_WARNING = 1;

	public static void DisplayInfoAlert(String title, String text,
			int alertType, ActionBarActivity activity) {
		AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(text);

		// Setting Icon to Dialog
		switch (alertType) {
		case TYPE_INFO:
			alertDialog.setIcon(R.drawable.ic_alert_info);
			break;
		case TYPE_WARNING:
			alertDialog.setIcon(R.drawable.ic_alert_warning);
			break;
		}

		// Setting OK Button
		alertDialog.setButton(activity.getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}
}
