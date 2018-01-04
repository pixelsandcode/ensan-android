package ir.app.ensan.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import ir.app.ensan.EnsanApp;
import ir.app.ensan.R;
import ir.app.ensan.component.view.FontType;

/**
 * Created by k.monem on 6/25/2016.
 */
public class SnackUtil {

  public static void makeSnackBar(final Context context, View view, int duration, String message,
      boolean useParams, CharSequence action, View.OnClickListener listener) {
    if (context == null) {
      return;
    }

    Snackbar snack = Snackbar.make(view, message, duration).setAction(action, listener);
    View v = snack.getView();

    final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();

    if (useParams) {
      params.setMargins(params.leftMargin, params.topMargin, params.rightMargin,
          params.bottomMargin + getSoftButtonsBarHeight(context));
      v.setLayoutParams(params);
    }

    TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
    TextView actionTextView =
        (TextView) v.findViewById(android.support.design.R.id.snackbar_action);
    textView.setTypeface(EnsanApp.getTypeFace(FontType.NORMAL));
    actionTextView.setTypeface(EnsanApp.getTypeFace(FontType.MEDIUM));

    textView.setTextSize(12);
    actionTextView.setGravity(Gravity.RIGHT);
    snack.show();
  }

  public static void makeSnackBar(final Context context, View view, int duration, String message,
      boolean useParams) {
    if (context == null) {
      return;
    }

    makeSnackBar(context, view, duration, message, useParams, "", null);

    //Snackbar snack = Snackbar.make(view, message, duration);
    //View v = snack.getView();
    //
    //TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
    //TextView actionTextView =
    //    (TextView) v.findViewById(android.support.design.R.id.snackbar_action);
    //textView.setTypeface(EnsanApp.getTypeFace(FontType.NORMAL));
    //actionTextView.setTypeface(EnsanApp.getTypeFace(FontType.MEDIUM));
    //snack.show();
  }

  public static void makeNetworkDisconnectSnackBar(final Context context, View view,boolean useParams) {
    makeSnackBar(context, view, Snackbar.LENGTH_LONG,
        context.getString(R.string.internet_disconnect), useParams,
        context.getString(R.string.network_setting), new View.OnClickListener() {
          @Override public void onClick(View v) {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            context.startActivity(intent);
          }
        });
  }
  public static void makeNetworkDisconnectSnackBar(final Context context, View view) {
    makeSnackBar(context, view, Snackbar.LENGTH_LONG,
        context.getString(R.string.internet_disconnect), true,
        context.getString(R.string.network_setting), new View.OnClickListener() {
          @Override public void onClick(View v) {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            context.startActivity(intent);
          }
        });
  }

  @SuppressLint("NewApi") private static int getSoftButtonsBarHeight(Context context) {
    // getRealMetrics is only available with API 17 and +
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      DisplayMetrics metrics = new DisplayMetrics();
      WindowManager windowManager =
          (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      windowManager.getDefaultDisplay().getMetrics(metrics);
      int usableHeight = metrics.heightPixels;
      windowManager.getDefaultDisplay().getRealMetrics(metrics);
      int realHeight = metrics.heightPixels;
      if (realHeight > usableHeight) {
        return realHeight - usableHeight;
      } else {
        return 0;
      }
    }
    return 0;
  }
}


