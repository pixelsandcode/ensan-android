package ir.app.ensan.component.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by k.monem on 6/2/2016.
 */
public class CustomRecycleView extends RecyclerView {

  private View emptyView;

  public CustomRecycleView(Context context) {
    super(context);
  }

  public CustomRecycleView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomRecycleView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setEmptyView(View emptyView) {
    this.emptyView = emptyView;
    this.emptyView.setVisibility(VISIBLE);
    setVisibility(GONE);
  }

  public void setEmptyViewVisibility(int visibility) {
    this.emptyView.setVisibility(visibility);
    setVisibility(visibility == VISIBLE ? GONE : VISIBLE);
  }
}
