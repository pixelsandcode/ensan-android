package ir.app.ensan.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import ir.app.ensan.R;
import ir.app.ensan.component.abstraction.GuardianListener;
import ir.app.ensan.component.view.viewholder.AddGuardianViewHolder;
import ir.app.ensan.component.view.viewholder.GuardianViewHolder;
import ir.app.ensan.model.ContactEntity;
import java.util.ArrayList;

/**
 * Created by Khashayar on 30/12/2017.
 */

public class GuardianListAdapter extends RecyclerView.Adapter<GuardianViewHolder> {

  private Context context;
  private LayoutInflater layoutInflater;
  private ArrayList<ContactEntity> guardianList;

  private GuardianListener guardianListener;

  public GuardianListAdapter(Context context, GuardianListener guardianListener) {
    this.context = context;
    this.guardianList = new ArrayList<>();
    this.layoutInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.guardianListener = guardianListener;
  }

  public void setGuardianList(ArrayList<ContactEntity> guardianList) {
    this.guardianList = guardianList;
  }

  @Override public GuardianViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    if (viewType == 0) {
      return new GuardianViewHolder(context,
          layoutInflater.inflate(R.layout.guardian_row, parent, false), guardianListener);
    } else {
      return new AddGuardianViewHolder(context,
          layoutInflater.inflate(R.layout.add_guardian_row, parent, false), guardianListener);
    }
  }

  @Override public void onBindViewHolder(GuardianViewHolder holder, int position) {
    if (position == guardianList.size()) {
      holder.bind(null, position);
    } else {
      holder.bind(guardianList.get(position), position);
    }
  }

  @Override public int getItemViewType(int position) {
    return position < guardianList.size() ? 0 : 1;
  }

  @Override public int getItemCount() {
    return guardianList.size() + 1;
  }
}
