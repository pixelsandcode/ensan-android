package ir.app.ensan.component.view.viewholder;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import ir.app.ensan.R;
import ir.app.ensan.component.abstraction.GuardianListener;
import ir.app.ensan.component.view.CustomTextView;
import ir.app.ensan.model.ContactEntity;

/**
 * Created by Khashayar on 30/12/2017.
 */

public class GuardianViewHolder extends CustomRecycleViewHolder {

  private AppCompatCheckBox invitaionCheckBox;
  private CustomTextView contactNameTextView;
  private CustomTextView contactNumberTextView;
  private CustomTextView guardianNotAnswerTextView;

  private GuardianListener guardianListener;

  public GuardianViewHolder(Context context, View itemView, GuardianListener guardianListener) {
    super(context, itemView);
    this.guardianListener = guardianListener;
  }

  @Override public void registerWidgets(View view) {
    invitaionCheckBox = (AppCompatCheckBox) itemView.findViewById(R.id.invitation_check_box);
    contactNameTextView = (CustomTextView) itemView.findViewById(R.id.contact_name);
    contactNumberTextView = (CustomTextView) itemView.findViewById(R.id.contact_phone_number);
    guardianNotAnswerTextView =
        (CustomTextView) itemView.findViewById(R.id.guardian_not_answer_text_view);
  }

  @Override public void bind(Object model, int position) {

    if (!(model instanceof ContactEntity)) {
      return;
    }

    final ContactEntity contactEntity = (ContactEntity) model;
    contactNameTextView.setText(contactEntity.getName());
    contactNumberTextView.setText(contactEntity.getPhoneNumber());

    guardianNotAnswerTextView.setVisibility(
        contactEntity.isInvitationAccepted() ? View.GONE : View.VISIBLE);

    invitaionCheckBox.setChecked(!contactEntity.isInvitationAccepted());

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        if (contactEntity.isInvitationAccepted()) {
          return;
        }

        guardianListener.onGuardianClick(contactEntity);
      }
    });
  }
}
