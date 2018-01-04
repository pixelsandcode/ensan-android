package ir.app.ensan.model.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Khashayar on 03/01/2018.
 */

public class GuardianListResponse extends BaseResponse {

  @SerializedName("data") @Expose private List<Guardian> data = null;

  public List<Guardian> getData() {
    return data;
  }

  public void setData(List<Guardian> data) {
    this.data = data;
  }

  public class Guardian {

    @SerializedName("docKey") @Expose private String docKey;
    @SerializedName("mobile") @Expose private String mobile;
    @SerializedName("name") @Expose private String name;
    @SerializedName("state") @Expose private String state;

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
  }
}
