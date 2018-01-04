package ir.app.ensan.component.abstraction;

import ir.app.ensan.model.ContactEntity;

/**
 * Created by Khashayar on 31/12/2017.
 */

public interface GuardianListener {

  void onGuardianClick(ContactEntity contactEntity);

  void onAddGuardianClick();

}
