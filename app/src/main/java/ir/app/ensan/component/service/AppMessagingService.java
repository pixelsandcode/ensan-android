package ir.app.ensan.component.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import ir.app.ensan.common.FireBaseManager;
import ir.app.ensan.common.NotificationManager;
import ir.app.ensan.component.receiver.FirebaseDataReceiver;
import ir.app.ensan.util.LogUtil;

public class AppMessagingService extends IntentService {

  public static final String NOTIFICATION_DATA = "notification_data";
  public static final String NOTIFICATION_TYPE_KEY = "type";
  public static final String NOTIFICATION_NAME_KEY = "name";
  public static final String NOTIFICATION_TIME_KEY = "at";
  public static final String NOTIFICATION_PHONE_NUMBER_KEY = "mobile";
  public static final String NOTIFICATION_PENDING_GUARDIAN_NAME_KEY = "pending";
  public static final String NOTIFICATION_SAFE_STATUS_KEY = "healthy";
  public static final String NOTIFICATION_DANGER_STATUS_KEY = "inDanger";
  public static final String NOTIFICATION_PENDING_GUARDIAN_KEY = "notifyInviter";
  public static final String NOTIFICATION_GENERAL_TITLE = "title";
  public static final String NOTIFICATION_GENERAL_BODY = "body";
  public static final String NOTIFICATION_LAT = "lat";
  public static final String NOTIFICATION_LON = "lon";

  public AppMessagingService() {
    super("AppMessagingService");
  }

  @Override protected void onHandleIntent(Intent intent) {

    FireBaseManager.getInstance().init();
    Bundle notificationData = intent.getBundleExtra(NOTIFICATION_DATA);

    String type = notificationData.getString(NOTIFICATION_TYPE_KEY);

    if (type == null) {
      type = "";
    }

    switch (type) {
      case NOTIFICATION_SAFE_STATUS_KEY:
        NotificationManager.getInstance()
            .fireSafeNotification(notificationData.getString(NOTIFICATION_NAME_KEY),
                notificationData.getString(NOTIFICATION_TIME_KEY));
        return;
      case NOTIFICATION_DANGER_STATUS_KEY:
        NotificationManager.getInstance()
            .fireWarningNotification(notificationData.getString(NOTIFICATION_NAME_KEY),
                notificationData.getString(NOTIFICATION_TIME_KEY),
                notificationData.getString(NOTIFICATION_PHONE_NUMBER_KEY),
                notificationData.getString(NOTIFICATION_LAT),
                notificationData.getString(NOTIFICATION_LON));
        break;

      case NOTIFICATION_PENDING_GUARDIAN_KEY:
        NotificationManager.getInstance()
            .firePendingGuardianNotification(
                notificationData.getString(NOTIFICATION_PENDING_GUARDIAN_NAME_KEY));
        break;
      default:
        LogUtil.logI("general title", notificationData.getString(NOTIFICATION_GENERAL_TITLE));
        LogUtil.logI("general body", notificationData.getString(NOTIFICATION_GENERAL_BODY));

        Intent activityIntent = new Intent("android.intent.action.MAIN");
        activityIntent.putExtra(NOTIFICATION_DATA, notificationData);

        sendBroadcast(activityIntent);
        break;
    }

    FirebaseDataReceiver.completeWakefulIntent(intent);
  }
}
