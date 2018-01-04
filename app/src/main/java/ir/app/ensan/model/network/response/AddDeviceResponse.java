package ir.app.ensan.model.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Khashayar on 03/01/2018.
 */

public class AddDeviceResponse extends BaseResponse {
  @SerializedName("data") @Expose private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public class Data {

    @SerializedName("docKey") @Expose private String docKey;
    @SerializedName("userKey") @Expose private String userKey;
    @SerializedName("devices") @Expose private List<String> devices = null;
    @SerializedName("success") @Expose private Boolean success;

    public String getDocKey() {
      return docKey;
    }

    public void setDocKey(String docKey) {
      this.docKey = docKey;
    }

    public String getUserKey() {
      return userKey;
    }

    public void setUserKey(String userKey) {
      this.userKey = userKey;
    }

    public List<String> getDevices() {
      return devices;
    }

    public void setDevices(List<String> devices) {
      this.devices = devices;
    }

    public Boolean getSuccess() {
      return success;
    }

    public void setSuccess(Boolean success) {
      this.success = success;
    }
  }
}
