package ir.app.ensan.common;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ir.app.ensan.R;
import ir.app.ensan.component.abstraction.SmsListener;
import ir.app.ensan.model.ContactEntity;
import ir.app.ensan.util.LogUtil;
import ir.app.ensan.util.SharedPreferencesUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Khashayar on 30/12/2017.
 */

public class ContactManager {

  private final String GUARDIAN_SAVE_KEY = "guardian_key";

  private static ContactManager instance;

  private Context context;
  private Set<ContactEntity> selectedContacts;

  private final String[] PROJECTION = {
      ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
      ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.CommonDataKinds.Phone.NUMBER,
      ContactsContract.CommonDataKinds.Phone.TYPE
  };

  public static ContactManager getInstance(Context context) {

    if (instance == null) {
      instance = new ContactManager(context);
    }
    return instance;
  }

  public ContactManager(Context context) {
    this.context = context;
    selectedContacts = new HashSet<>();
    loadContacts();
  }

  public ArrayList<ContactEntity> getAllContacts() {
    ArrayList<ContactEntity> contactEntities = new ArrayList<>();

    Cursor cursor = context.getContentResolver()
        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);

    if (cursor == null) {
      return new ArrayList<>();
    }

    cursor.moveToPosition(-1);

    while (cursor.moveToNext()) {

      int phoneType =
          cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
      if (phoneType != ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
        continue;
      }

      ContactEntity contactEntity =
          new ContactEntity(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
              cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
              cursor.getString(
                  cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

      LogUtil.logI("phone type", contactEntity.getPhoneNumber() + " " + phoneType);
      if (contactAlreadyExist(contactEntity.getPhoneNumber())) {
        continue;
      }

      contactEntity.setPhoneNumber(contactEntity.getPhoneNumber().replaceAll("\\s+",""));
      contactEntity.setPhoneNumber(contactEntity.getPhoneNumber().replaceAll("[\\s\\-()]",""));
      contactEntities.add(contactEntity);
    }
    cursor.close();

    return contactEntities;
  }

  public void setSelectedContacts(Set<ContactEntity> selectedContacts) {
    this.selectedContacts = selectedContacts;
  }

  public void addSelectedContacts(ArrayList<ContactEntity> newContacts) {
    this.selectedContacts.addAll(newContacts);
  }

  public void addSelectedContact(ContactEntity newContacts) {
    this.selectedContacts.add(newContacts);
  }

  public Set<ContactEntity> getSelectedContacts() {
    return selectedContacts;
  }

  private boolean contactAlreadyExist(String phoneNumber) {
    for (ContactEntity contactEntity : selectedContacts) {
      if (phoneNumber.equals(contactEntity.getPhoneNumber())) {
        return true;
      }
    }
    return false;
  }

  public void saveContacts() {
    String guardianInJson = new Gson().toJson(getSelectedContacts());
    SharedPreferencesUtil.saveString(GUARDIAN_SAVE_KEY, guardianInJson);
  }

  public void loadContacts() {
    String loadedGuardian = SharedPreferencesUtil.loadString(GUARDIAN_SAVE_KEY, "");

    if (loadedGuardian.isEmpty()) {
      return;
    }

    selectedContacts = new Gson().fromJson(loadedGuardian, new TypeToken<Set<ContactEntity>>() {
    }.getType());
  }

  public boolean isContactExist() {
    loadContacts();
    return selectedContacts != null && selectedContacts.size() >= 3;
  }

  public boolean isAnyContactExist() {
    loadContacts();
    return selectedContacts != null && !selectedContacts.isEmpty();
  }

  public Set<ContactEntity> getGuardians() {
    loadContacts();
    return selectedContacts;
  }
  public void getGuardianSize() {
    selectedContacts.size();
  }

  public void sendMessage(ContactEntity contactEntity, SmsListener smsListener) {
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(contactEntity.getPhoneNumber(), null,
          context.getString(R.string.invitation_message), null, null);
      smsListener.onSmsSent(contactEntity);
    } catch (Exception ex) {
      smsListener.onSmsNotSent(contactEntity);
      ex.printStackTrace();
    }
  }
}
