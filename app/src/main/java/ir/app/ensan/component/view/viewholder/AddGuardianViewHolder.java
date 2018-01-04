package ir.app.ensan.component.view.viewholder;

import android.content.Context;
import android.view.View;
import ir.app.ensan.component.abstraction.GuardianListener;

/**
 * Created by Khashayar on 30/12/2017.
 */

public class AddGuardianViewHolder extends GuardianViewHolder {

  private GuardianListener guardianListener;

  public AddGuardianViewHolder(Context context, View itemView, GuardianListener guardianListener) {
    super(context,itemView,guardianListener);
    this.guardianListener = guardianListener;
  }

  @Override public void registerWidgets(View view) {
  }

  @Override public void bind(Object model, int position) {

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        guardianListener.onAddGuardianClick();
      }
    });
  }
}
