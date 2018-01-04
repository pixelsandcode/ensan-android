package ir.app.ensan.model;

import ir.app.ensan.model.network.response.GuardianListResponse;

/**
 * Created by k.monem on 7/18/2016.
 */
public class ContactEntity {

  private long id;
  private String name;
  private String phoneNumber;
  private boolean invitationAccepted;

  public ContactEntity(long id, String name, String phoneNumber) {
    this.id = id;
    this.name = name;
    this.phoneNumber = phoneNumber;
  }

  public ContactEntity(GuardianListResponse.Guardian guardian) {
    //this.id;
    this.name = guardian.getName();
    this.phoneNumber = guardian.getMobile();
    this.invitationAccepted = !"pending".equals(guardian.getState());
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public boolean isInvitationAccepted() {
    return invitationAccepted;
  }

  public void setInvitationAccepted(boolean invitationAccepted) {
    this.invitationAccepted = invitationAccepted;
  }
}
