package ir.app.ensan.component.abstraction;

import ir.app.ensan.model.ContactEntity;

/**
 * Created by Khashayar on 03/01/2018.
 */

public interface SmsListener {

  void onSmsSent(ContactEntity contactEntity);

  void onSmsNotSent(ContactEntity contactEntity);

}
