package com.ojambrina.ipatient.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ojambrina.ipatient.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.ojambrina.ipatient.utils.Constants.PATTERN;

public class Utils {

    public static ProgressDialog showProgressDialog(Context context, String message, int appCompatAlertDialogStyle) {
        ProgressDialog progressDialog = new ProgressDialog(context, appCompatAlertDialogStyle);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

        return progressDialog;
    }

    public static Dialog openDialog(Context context, int layout) {
        Dialog dialog = new Dialog(context);

        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public static Spanned fromHtml(String text) {
        Spanned textSpanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textSpanned = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
            Log.d("STRING SPANNED", textSpanned.toString());
        } else {
            textSpanned = Html.fromHtml(text);
        }
        return textSpanned;
    }

    public static void loadGlide(@NonNull Context context, @NonNull String url, @NonNull ImageView view) {
        Activity activity = (Activity) context;
        if (!activity.isFinishing() && view != null) {
            Glide.with(context)
                    .load(url)
                    .into(view);
        }
    }

    public static void configToolbar(AppCompatActivity context, Toolbar toolbar) {
        context.setSupportActionBar(toolbar);
        context.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context.getSupportActionBar().setDisplayShowHomeEnabled(true);
        context.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(context.getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        /*final Drawable customArrow = context.getResources().getDrawable(R.drawable.back);
        context.getSupportActionBar().setHomeAsUpIndicator(customArrow);*/
    }

    public static String getFormattedDate(long millis)  {
        DateFormat date = new SimpleDateFormat(PATTERN);
        String localTime = date.format(millis);

        return localTime;
    }

    public static String getCurrentDay()  {
        long millis = System.currentTimeMillis();
        DateFormat date = new SimpleDateFormat(PATTERN);
        String localTime = date.format(millis);

        return localTime;
    }

    public static Long formatMillis(String time, String dateFormat) throws ParseException {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(timeZone);
        Date date = simpleDateFormat.parse(time);
        return date.getTime();
    }

    public static void permissionDialog(final Context context) {
        final Dialog dialog = openDialog(context, R.layout.dialog_permissions);

        TextView yes = dialog.findViewById(R.id.yes);
        TextView no = dialog.findViewById(R.id.no);
        TextView title = dialog.findViewById(R.id.title);
        TextView text = dialog.findViewById(R.id.text);

        title.setText(context.getString(R.string.permission_needed));
        text.setText(context.getString(R.string.grant_permission));
        yes.setText(context.getString(R.string.text_accept));
        no.setText(context.getString(R.string.text_cancel));

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
