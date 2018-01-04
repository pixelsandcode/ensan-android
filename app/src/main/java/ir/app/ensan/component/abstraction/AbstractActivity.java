package ir.app.ensan.component.abstraction;

/**
 * Created by Khashayar on 21/09/2017.
 */

public interface AbstractActivity {

  void registerWidgets();

  void setListeners();

  void initBroadcastReceiver();

  void setMenuListeners();

  void getExtra();

  void setAdapter();

  void setRecycleAdapter();

  void initRecycleView();

  void notifyRecycleAdapter();

  void onInternetConnected();

  void setEmptyView();

  void progressStart();

  void progressFinish();
}
