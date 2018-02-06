package ir.app.ensan.model.network.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khashayar on 1/23/2018.
 */

public class Guardian {

  @SerializedName("name") @Expose private String name;
  @SerializedName("mobile") @Expose private String mobile;

  private Guardian(Builder builder) {
    setName(builder.name);
    setMobile(builder.mobile);
  }

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

  public static final class Builder {
    private String name;
    private String mobile;

    public Builder() {
    }

    public Builder name(String val) {
      name = val;
      return this;
    }

    public Builder mobile(String val) {
      mobile = val;
      return this;
    }

    public Guardian build() {
      return new Guardian(this);
    }
  }
}


