package ir.app.ensan.common;

import android.content.Context;
import android.location.Location;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import ir.app.ensan.util.LogUtil;

/**
 * Created by Khashayar on 1/23/2018.
 */

public class LocationManager implements OnLocationUpdatedListener {

  private static LocationManager instance;

  private Context context;

  private LocationGooglePlayServicesProvider provider;
  private LocationParams.Builder builder;
  private Location location = null;

  public static LocationManager getInstance(Context context) {
    if (instance == null) {
      instance = new LocationManager(context);
    }
    return instance;
  }

  public LocationManager(Context context) {
    this.context = context;
    provider = new LocationGooglePlayServicesProvider();
    provider.setCheckLocationSettings(true);

    setConfig();
  }

  private void setConfig() {
    builder = new LocationParams.Builder().setAccuracy(LocationAccuracy.LOWEST)
        .setDistance(0)
        .setInterval(5000);

    LogUtil.logI("locationServicesEnabled",
        SmartLocation.with(context).location().state().locationServicesEnabled() + "");
    LogUtil.logI("isAnyProviderAvailable",
        SmartLocation.with(context).location().state().isAnyProviderAvailable() + "");
    LogUtil.logI("isGpsAvailable",
        SmartLocation.with(context).location().state().isGpsAvailable() + "");
    LogUtil.logI("isNetworkAvailable",
        SmartLocation.with(context).location().state().isNetworkAvailable() + "");

    SmartLocation smartLocation = new SmartLocation.Builder(context).logging(true).build();
    smartLocation.location(provider).oneFix().start(this);
  }

  @Override public void onLocationUpdated(Location location) {

    LogUtil.logI("new location", location.getLongitude() + " " + location.getLongitude());
  }

  public Location getLocation() {
    return location;
  }
}
