package ir.app.ensan.model.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khashayar on 21/09/2017.
 */

public class BaseResponse {

  @SerializedName("statusCode") @Expose private Integer statusCode;
  @SerializedName("error") @Expose private Error error;
  @SerializedName("message") @Expose private String message;

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public class Error {

    @SerializedName("payload") @Expose private Payload payload;
    @SerializedName("api") @Expose private String api;

    public Payload getPayload() {
      return payload;
    }

    public void setPayload(Payload payload) {
      this.payload = payload;
    }

    public String getApi() {
      return api;
    }

    public void setApi(String api) {
      this.api = api;
    }

    public class Payload {

      @SerializedName("name") @Expose private String name;
      @SerializedName("mobile") @Expose private String mobile;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getMobile() {
        return mobile;
      }

      public void setMobile(String mobile) {
        this.mobile = mobile;
      }
    }
  }
}
