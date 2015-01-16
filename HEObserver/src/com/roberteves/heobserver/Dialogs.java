package com.roberteves.heobserver;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class Dialogs {
	public void DisplayInfoAlert(String title, String text) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				Global.APP_CONTEXT);
		alertDialog.setTitle(title);
		alertDialog.setMessage(text);
		alertDialog.setIcon(R.drawable.ic_alert_info);

		alertDialog.setPositiveButton(
				Global.APP_CONTEXT.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		alertDialog.show();
	}

}
