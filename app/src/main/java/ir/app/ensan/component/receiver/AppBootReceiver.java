package ir.app.ensan.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ir.app.ensan.BuildConfig;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.common.NotificationManager;
import ir.app.ensan.util.LogUtil;
import ir.app.ensan.util.TimeUtil;

/**
 * Created by k.monem on 10/15/2016.
 */
public class AppBootReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {

    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      LogUtil.logE("on boot", "YES");

      if (ContactManager.getInstance(context).isAnyContactExist()){
        return;
      }

      NotificationManager.getInstance().scheduleNotification(BuildConfig.STG?
          TimeUtil.getCurrentDate()+10000 : TimeUtil.addDayFromNow(1));// first day
      NotificationManager.getInstance().scheduleNotification(TimeUtil.addDayFromNow(3));// first day
      NotificationManager.getInstance().scheduleNotification(TimeUtil.addDayFromNow(7));// first day
    }
  }
}
