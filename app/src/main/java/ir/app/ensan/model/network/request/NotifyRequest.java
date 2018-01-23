package ir.app.ensan.model.network.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khashayar on 03/01/2018.
 */

public class NotifyRequest extends BaseRequest {

  @SerializedName("type") @Expose private String type;
  @SerializedName("location") @Expose private Location location;

  private NotifyRequest(Builder builder) {
    setType(builder.type);

    if (builder.lat == 0 || builder.lon == 0) {
      return;
    }

    Location location = new Location();
    location.lat = builder.lat;
    location.lon = builder.lon;
    setLocation(location);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public class Location {

    @SerializedName("lat") @Expose private Double lat;
    @SerializedName("lon") @Expose private Double lon;

    public Double getLat() {
      return lat;
    }

    public void setLat(Double lat) {
      this.lat = lat;
    }

    public Double getLon() {
      return lon;
    }

    public void setLon(Double lon) {
      this.lon = lon;
    }
  }

  public static final class Builder {
    private String type;
    private double lat;
    private double lon;

    public Builder() {
    }

    public Builder type(String val) {
      type = val;
      return this;
    }

    public Builder lat(double val) {
      lat = val;
      return this;
    }

    public Builder lon(double val) {
      lon = val;
      return this;
    }

    public NotifyRequest build() {
      return new NotifyRequest(this);
    }
  }
}
