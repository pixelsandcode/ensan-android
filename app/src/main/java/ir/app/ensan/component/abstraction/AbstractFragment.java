package ir.app.ensan.component.abstraction;

/**
 * Created by Khashayar on 21/09/2017.
 */

public interface AbstractFragment {

  void registerWidgets();

  void setListeners();

  void getExtra();

  void setAdapter();

  void notifyAdapter();

  void setRecycleAdapter();

  void initRecycleView();

  void notifyRecycleAdapter();
}

