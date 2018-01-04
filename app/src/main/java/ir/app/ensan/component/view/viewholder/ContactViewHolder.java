package ir.app.ensan.component.view.viewholder;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;
import ir.app.ensan.R;
import ir.app.ensan.component.abstraction.ContactSelectListener;
import ir.app.ensan.component.adapter.ContactSelectAdapter;
import ir.app.ensan.component.view.CustomTextView;
import ir.app.ensan.model.ContactEntity;

/**
 * Created by Khashayar on 30/12/2017.
 */

public class ContactViewHolder extends CustomRecycleViewHolder {

  private AppCompatCheckBox selectCheckBox;
  private CustomTextView contactNameTextView;
  private CustomTextView contactNumberTextView;

  private ContactSelectListener contactSelectListener;



  //private boolean selected = false;

  public ContactViewHolder(Context context, View itemView,
      ContactSelectListener contactSelectListener) {
    super(context, itemView);
    this.setIsRecyclable(false);
    this.contactSelectListener = contactSelectListener;
  }

  @Override public void registerWidgets(View view) {
    contactNameTextView = (CustomTextView) itemView.findViewById(R.id.contact_name);
    contactNumberTextView = (CustomTextView) itemView.findViewById(R.id.contact_phone_number);
    selectCheckBox = (AppCompatCheckBox) itemView.findViewById(R.id.contact_check_box);
  }

  @Override public void bind(Object model, final int position) {

    if (!(model instanceof ContactEntity)) {
      return;
    }

    final ContactEntity contactEntity = (ContactEntity) model;
    contactNameTextView.setText(contactEntity.getName());
    contactNumberTextView.setText(contactEntity.getPhoneNumber());

    selectCheckBox.setChecked(position == ContactSelectAdapter.selectedIndex);

    selectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean selected) {
        if (selected) {
          contactSelectListener.onContactSelect(contactEntity);
          ContactSelectAdapter.selectedIndex = position;
        } else {
          contactSelectListener.onContactDeSelect(contactEntity);
        }
        selectCheckBox.setEnabled(!selected);
      }
    });
  }
}
