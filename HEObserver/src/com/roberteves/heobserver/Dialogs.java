package com.roberteves.heobserver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;

public class Dialogs {
	public static void DisplayInfoAlert(String title, String text,
			ActionBarActivity activity) {
		AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(text);

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.ic_alert_info); //TODO 1.0 reduce icon size

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
