package ir.app.ensan.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import ir.app.ensan.R;
import ir.app.ensan.component.abstraction.ContactSelectListener;
import ir.app.ensan.component.view.viewholder.ContactViewHolder;
import ir.app.ensan.model.ContactEntity;
import java.util.ArrayList;

/**
 * Created by Khashayar on 30/12/2017.
 */

public class ContactSelectAdapter extends RecyclerView.Adapter<ContactViewHolder>  {

  private Context context;
  private LayoutInflater layoutInflater;
  private ArrayList<ContactEntity> contactEntities;

  private ContactSelectListener contactSelectListener;

  public static int selectedIndex = -1;

  public ContactSelectAdapter(Context context,ContactSelectListener contactSelectListener) {
    this.context = context;
    this.contactEntities = new ArrayList<>();
    this.layoutInflater = (LayoutInflater)
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.contactSelectListener = contactSelectListener;
  }

  public void setContactEntities(ArrayList<ContactEntity> contactEntities) {
    this.contactEntities = contactEntities;
  }

  @Override public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ContactViewHolder(context,
        layoutInflater.inflate(R.layout.contact_row, parent, false),contactSelectListener);
  }

  @Override public void onBindViewHolder(ContactViewHolder holder, int position) {
    holder.bind(contactEntities.get(position), position);
  }

  @Override public int getItemCount() {
    return contactEntities.size();
  }
}
