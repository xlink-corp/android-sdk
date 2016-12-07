package io.xlink.wifi.pipe.view;

import io.xlink.wifi.pipe.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class CustomDialog extends Dialog {

    private CustomDialog(Context context, int width, int height, int layou) {

	super(context, R.style.Theme_dialog);
	// set content
	setContentView(layou);
	Window window = getWindow();
	WindowManager.LayoutParams params = window.getAttributes();
	Resources resources = context.getResources();
	DisplayMetrics dm = resources.getDisplayMetrics();
	params.width = width;
	params.height = height;
	params.gravity = Gravity.CENTER;
	window.setAttributes(params);
    }

    public static CustomDialog createBigDialog(Context context, int layou) {
	Resources resources = context.getResources();
	DisplayMetrics dm = resources.getDisplayMetrics();
	int w = (int) (dm.widthPixels * 0.85);
	int h = (int) (dm.heightPixels * 0.55);

	return new CustomDialog(context, w, h, layou);
    }

    public static CustomDialog createErrorDialog(Context context, String title,
	    String msg, View.OnClickListener listener) {
	Resources resources = context.getResources();
	DisplayMetrics dm = resources.getDisplayMetrics();
	int w = (int) (dm.widthPixels * 0.85);
	int h = (int) (dm.heightPixels * 0.25);
	final CustomDialog dialog = new CustomDialog(context, w, h,
		R.layout.xlink_dialog_view);
	TextView title_tv = (TextView) dialog.findViewById(R.id.dialog_title);
	TextView msg_tv = (TextView) dialog.findViewById(R.id.dialog_msg);
	if (listener == null) {
	    dialog.findViewById(R.id.dialog_cancel).setOnClickListener(
		    new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			    dialog.dismiss();
			}
		    });
	} else {
	    dialog.findViewById(R.id.dialog_cancel)
		    .setOnClickListener(listener);

	}
	title_tv.setText(title);
	msg_tv.setText(msg);
	return dialog;
    }

    public static CustomDialog createStandardDialog(Context context, int layou) {
	Resources resources = context.getResources();
	DisplayMetrics dm = resources.getDisplayMetrics();
	int w = (int) (dm.widthPixels * 0.85);
	int h = (int) (dm.heightPixels * 0.55);

	return new CustomDialog(context, w, h, layou);
    }

    public static CustomDialog createMicroDialog(Context context, int layou) {
	Resources resources = context.getResources();
	DisplayMetrics dm = resources.getDisplayMetrics();
	int w = (int) (dm.widthPixels * 0.85);
	int h = (int) (dm.heightPixels * 0.40);

	return new CustomDialog(context, w, h, layou);
    }

    public static ProgressDialog createProgressDialog(Context context,
	    String title, String tips) {
	ProgressDialog progressDialog = ProgressDialog.show(context, title,
		tips, true, true);
	progressDialog.setCanceledOnTouchOutside(false);
	return progressDialog;
    }

}