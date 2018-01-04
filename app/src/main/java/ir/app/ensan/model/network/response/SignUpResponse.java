package ir.app.ensan.model.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Khashayar on 30/12/2017.
 */

public class SignUpResponse extends BaseResponse {

  @SerializedName("data") @Expose private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public class Data {

    @SerializedName("docKey") @Expose private String docKey;
    @SerializedName("mobile") @Expose private String mobile;
    @SerializedName("name") @Expose private String name;
    @SerializedName("guardians") @Expose private List<Object> guardians = null;
    @SerializedName("state") @Expose private String state;
    @SerializedName("auth") @Expose private String auth;
    @SerializedName("success") @Expose private Boolean success;

    public String getDocKey() {
      return docKey;
    }

    public void setDocKey(String docKey) {
      this.docKey = docKey;
    }

    public String getMobile() {
      return mobile;
    }

    public void setMobile(String mobile) {
      this.mobile = mobile;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<Object> getGuardians() {
      return guardians;
    }

    public void setGuardians(List<Object> guardians) {
      this.guardians = guardians;
    }

    public String getState() {
      return state;
    }

    public void setState(String state) {
      this.state = state;
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
