package ir.app.ensan.common;

import android.app.Activity;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;
import ir.app.ensan.Config;
import ir.app.ensan.component.fragment.BaseFragment;

/**
 * Created by k.monem on 2/22/2017.
 */

public class FirebaseAnalyticManager {

  private static FirebaseAnalyticManager instance;

  private FirebaseAnalytics firebaseAnalytics;

  private FirebaseAnalyticManager(Activity activity) {
    firebaseAnalytics = FirebaseAnalytics.getInstance(activity);
  }

  public static FirebaseAnalyticManager getInstance(Activity activity) {

    if (instance == null) {
      instance = new FirebaseAnalyticManager(activity);
    }
    return instance;
  }

  public void sendActivityViewEvent(Activity activity) {

    if (!Config.STATISTIC_ANALYSER_ENABLE) {
      return;
    }

    Bundle params = new Bundle();
    params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Activity");
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, getClassName(activity.getClass()));
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
  }

  public void sendFragmentViewEvent(BaseFragment fragment) {

    if (!Config.STATISTIC_ANALYSER_ENABLE) {
      return;
    }

    Bundle params = new Bundle();
    params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Fragment");
    params.putString(FirebaseAnalytics.Param.ITEM_NAME, getClassName(fragment.getClass()));
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
  }

  private String getClassName(Class<?> appClass) {
    return appClass.getClass().getSimpleName();
  }
}
