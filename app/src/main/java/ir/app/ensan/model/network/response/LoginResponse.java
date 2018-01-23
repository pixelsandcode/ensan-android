package ir.app.ensan.model.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khashayar on 03/01/2018.
 */

public class LoginResponse extends BaseResponse {

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
    @SerializedName("state") @Expose private String state;
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

    public String getState() {
      return state;
    }

    public void setState(String state) {
      this.state = state;
    }

    public Boolean getSuccess() {
      return success;
    }

    public void setSuccess(Boolean success) {
      this.success = success;
    }
  }
}
