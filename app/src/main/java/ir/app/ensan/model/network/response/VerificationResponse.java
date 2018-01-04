package ir.app.ensan.model.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khashayar on 03/01/2018.
 */

public class VerificationResponse extends BaseResponse {

  @SerializedName("data") @Expose private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public class Data {

    @SerializedName("userKey") @Expose private String userKey;
    @SerializedName("auth") @Expose private String auth;
    @SerializedName("success") @Expose private Boolean success;

    public String getUserKey() {
      return userKey;
    }

    public void setUserKey(String userKey) {
      this.userKey = userKey;
    }

    public String getAuth() {
      return auth;
    }

    public void setAuth(String auth) {
      this.auth = auth;
    }

    public Boolean getSuccess() {
      return success;
    }

    public void setSuccess(Boolean success) {
      this.success = success;
    }
  }
}
