package ir.app.ensan.model.network.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Khashayar on 1/23/2018.
 */

public class AddGuardianRequest extends BaseRequest {

  @SerializedName("guardians") @Expose private List<Guardian> guardians = null;

  private AddGuardianRequest(Builder builder) {
    setGuardians(builder.guardians);
  }

  public List<Guardian> getGuardians() {
    return guardians;
  }

  public void setGuardians(List<Guardian> guardians) {
    this.guardians = guardians;
  }



  public static final class Builder {
    private List<Guardian> guardians;

    public Builder() {
    }

    public Builder guardians(List<Guardian> val) {
      guardians = val;
      return this;
    }

    public AddGuardianRequest build() {
      return new AddGuardianRequest(this);
    }
  }
}
