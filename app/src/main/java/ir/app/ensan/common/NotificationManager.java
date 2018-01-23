package ir.app.ensan.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ir.app.ensan.EnsanApp;
import ir.app.ensan.R;
import ir.app.ensan.component.activity.HomeActivity;
import ir.app.ensan.component.activity.SplashActivity;
import ir.app.ensan.component.receiver.NotificationReceiver;
import ir.app.ensan.util.TimeUtil;
import java.util.ArrayList;

/**
 * Created by k.monem on 9/22/2016.
 */
public class NotificationManager {

  private static NotificationManager instance;

  private Context context;
  private android.app.NotificationManager notificationManager;

  public NotificationManager() {
    this.context = EnsanApp.getAppContext();
    this.notificationManager =
        (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  public static NotificationManager getInstance() {

    if (instance == null) {
      instance = new NotificationManager();
    }
    return instance;
  }

  public void fireNoGuardianNotification(long notificationId) {

    if (ContactManager.getInstance(EnsanApp.getAppContext()).isAnyContactExist()) {
      return;
    }

    Intent intent = new Intent(context, SplashActivity.class);

    PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) notificationId, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);

    Notification notification;

    Notification.Builder notificationBuilder =
        new Notification.Builder(context).setContentTitle(context.getString(R.string.warning))
            .setContentText(context.getString(R.string.no_guardian_warning))
            .setSmallIcon(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.ic_launcher
                    : R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      notificationBuilder.setStyle(
          new Notification.BigTextStyle().bigText(context.getString(R.string.no_guardian_warning)));
      notification = notificationBuilder.build();
    } else {
      notification = notificationBuilder.getNotification();
    }

    notificationManager.notify((int) notificationId, notification);
  }

  public void firePendingGuardianNotification(String pendingNames) {

    if (pendingNames.isEmpty()) {
      return;
    }

    Intent intent = new Intent(context, HomeActivity.class);

    PendingIntent pendingIntent = PendingIntent.getActivity(context, getNotificationId(), intent,
        PendingIntent.FLAG_CANCEL_CURRENT);

    Notification notification;

    Notification.Builder notificationBuilder =
        new Notification.Builder(context).setContentTitle(context.getString(R.string.warning))
            .setContentText(getPendingGuardianNotificationBody(pendingNames))
            .setSmallIcon(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.ic_launcher
                    : R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(
          getPendingGuardianNotificationBody(pendingNames)));
      notification = notificationBuilder.build();
    } else {
      notification = notificationBuilder.getNotification();
    }

    notificationManager.notify(1, notification);
  }

  public void fireSafeNotification(String name, String time) {

    Notification notification;

    Notification.Builder notificationBuilder =
        new Notification.Builder(context).setContentTitle(getSafeNotificationTitle(name))
            .setContentText(getSafeNotificationBody(name, time))
            .setSmallIcon(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.ic_launcher
                    : R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      notificationBuilder.setStyle(
          new Notification.BigTextStyle().bigText(getSafeNotificationBody(name, time)));
      notification = notificationBuilder.build();
    } else {
      notification = notificationBuilder.getNotification();
    }

    notificationManager.notify(1, notification);
  }

  public void fireWarningNotification(String name, String time, String phoneNumber, String latitude,
      String longitude) {

    Notification notification;

    Notification.Builder notificationBuilder =
        new Notification.Builder(context).setContentTitle(getWarningNotificationTitle())
            .setContentText(getWarningNotificationBody(name, time))
            .setSmallIcon(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.ic_launcher
                    : R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

    Intent phoneCall = new Intent(Intent.ACTION_DIAL);
    phoneCall.setData((Uri.parse("tel:" + phoneNumber)));

    PendingIntent phoneCallIntent =
        PendingIntent.getActivity(context, 0, phoneCall, PendingIntent.FLAG_CANCEL_CURRENT);

    notificationBuilder.addAction(android.R.drawable.ic_menu_call, context.getString(R.string.call),
        phoneCallIntent);

    if (latitude != null && longitude != null) {

      String geoUri = "http://maps.google.com/maps?q=loc:"
          + latitude
          + ","
          + longitude
          + " ("
          + getLocationText(name, time)
          + ")";
      Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));

      PendingIntent mapPendingIntent =
          PendingIntent.getActivity(context, 0, mapIntent, PendingIntent.FLAG_CANCEL_CURRENT);

      notificationBuilder.addAction(android.R.drawable.ic_menu_mapmode,
          context.getString(R.string.show_location), mapPendingIntent);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      notificationBuilder.setStyle(
          new Notification.BigTextStyle().bigText(getWarningNotificationBody(name, time)));
      notification = notificationBuilder.build();
    } else {
      notification = notificationBuilder.getNotification();
    }

    notificationManager.notify(1, notification);
  }

  public void scheduleNotification(long notifyTime) {

    Intent notificationIntent = new Intent(context, NotificationReceiver.class);
    notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, getNotificationId());

    PendingIntent uniquePendingIntent =
        PendingIntent.getBroadcast(context, getNotificationId(), notificationIntent,
            PendingIntent.FLAG_ONE_SHOT);

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC_WAKEUP, notifyTime, uniquePendingIntent);
  }

  private String getSafeNotificationTitle(String name) {
    return name + " " + context.getString(R.string.is_safe);
  }

  private String getSafeNotificationBody(String name, String time) {
    return String.format(context.getString(R.string.safe_description), name, parseDate(time));
  }

  private String getWarningNotificationTitle() {
    return context.getString(R.string.warning);
  }

  private String getWarningNotificationBody(String name, String time) {
    return String.format(context.getString(R.string.in_danger_description), name, parseDate(time));
  }

  private String getLocationText(String name, String time) {
    return String.format(context.getString(R.string.location_text), name, parseDate(time));
  }

  private String getPendingGuardianNotificationBody(String pendingNames) {

    StringBuilder nameString = new StringBuilder("");
    ArrayList<String> names = new Gson().fromJson(pendingNames, new TypeToken<ArrayList<String>>() {
    }.getType());

    for (String name : names) {
      nameString.append(name).append(" ");
    }

    return String.format(context.getString(R.string.pending_guardian_warning),
        nameString.toString());
  }

  private String parseDate(String dateString) {
    return TimeUtil.getFormattedDate(dateString);
  }

  private int getNotificationId() {
    return (int) System.currentTimeMillis();
  }
}
