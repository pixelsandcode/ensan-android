package ir.app.ensan.model.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khashayar on 03/01/2018.
 */

public class NotifyResponse extends BaseResponse {

  @SerializedName("data") @Expose private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public class Data {

    @SerializedName("sendTo") @Expose private Integer sendTo;
    @SerializedName("success") @Expose private Boolean success;

    public Integer getSendTo() {
      return sendTo;
    }

    public void setSendTo(Integer sendTo) {
      this.sendTo = sendTo;
    }

    public Boolean getSuccess() {
      return success;
    }

    public void setSuccess(Boolean success) {
      this.success = success;
    }
  }
}
