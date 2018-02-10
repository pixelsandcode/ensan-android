package ir.app.ensan.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.View;
import ir.app.ensan.EnsanApp;
import ir.app.ensan.model.network.ServiceGenerator;
import ir.app.ensan.model.network.response.BaseResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by k.monem on 6/2/2016.
 */
public class NetworkUtil {

  public static final String AUT_KEY = "authorization_key";

  public static boolean isInternetConnected(Context context, View view) {
    if (isInternetConnected()) {
      return true;
    } else {
      SnackUtil.makeNetworkDisconnectSnackBar(context, view);
      return false;
    }
  }

  public static boolean isInternetConnected() {
    ConnectivityManager cm = (ConnectivityManager) EnsanApp.getAppContext()
        .getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo wifiNetwork = cm.getNetworkInfo(1);
    if (wifiNetwork != null && wifiNetwork.isConnected()) {
      return true;
    }
    NetworkInfo mobileNetwork = cm.getNetworkInfo(0);
    if (mobileNetwork != null && mobileNetwork.isConnected()) {
      return true;
    }
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (activeNetwork == null || !activeNetwork.isConnected()) {
      return false;
    }
    return true;
  }

  public static Boolean isGsmAvailable() {
    TelephonyManager tel = (TelephonyManager) EnsanApp.getAppContext().
        getSystemService(Context.TELEPHONY_SERVICE);
    return (!(tel.getNetworkOperator() != null && tel.getNetworkOperator().equals("")));
  }

  public static BaseResponse parseError(ResponseBody errorBody) {
    Converter<ResponseBody, BaseResponse> converter = ServiceGenerator.getInstance()
        .getRetrofit()
        .responseBodyConverter(BaseResponse.class, new Annotation[0]);

    BaseResponse error;

    try {
      error = converter.convert(errorBody);
    } catch (Exception e) {
      return new BaseResponse();
    }

    return error;
  }

  public static BaseResponse parseResponse(ResponseBody errorBody) {
    Converter<ResponseBody, BaseResponse> converter = ServiceGenerator.getInstance()
        .getRetrofit()
        .responseBodyConverter(BaseResponse.class, new Annotation[0]);

    BaseResponse baseResponse;

    try {
      baseResponse = converter.convert(errorBody);
    } catch (IOException e) {
      return new BaseResponse();
    }

    return baseResponse;
  }

  public static String getNetworkClass(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = cm.getActiveNetworkInfo();
    if (info == null || !info.isConnected()) return "-"; //not connected
    if (info.getType() == ConnectivityManager.TYPE_WIFI) return "WIFI";
    if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
      int networkType = info.getSubtype();
      switch (networkType) {
        case TelephonyManager.NETWORK_TYPE_GPRS:
        case TelephonyManager.NETWORK_TYPE_EDGE:
        case TelephonyManager.NETWORK_TYPE_CDMA:
        case TelephonyManager.NETWORK_TYPE_1xRTT:
        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
          return "2G";
        case TelephonyManager.NETWORK_TYPE_UMTS:
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_HSDPA:
        case TelephonyManager.NETWORK_TYPE_HSUPA:
        case TelephonyManager.NETWORK_TYPE_HSPA:
        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
          return "3G";
        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
          return "4G";
        default:
          return "UNKNW";
      }
    }
    return "?";
  }

  public static boolean canGetLocation() {
    boolean result = true;
    LocationManager locationManager;
    boolean gpsEnabled = false;
    boolean networkEnabled = false;

    locationManager =
        (LocationManager) EnsanApp.getAppContext().getSystemService(Context.LOCATION_SERVICE);

    // exceptions will be thrown if provider is not permitted.
    try {
      gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    } catch (Exception ex) {

    }
    try {
      networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    } catch (Exception ex) {
    }
    if (gpsEnabled == false || networkEnabled == false) {
      result = false;
    } else {
      result = true;
    }

    return result;
  }

  public static String getIPAddress(boolean useIPv4) {
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface intf : interfaces) {
        List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
        for (InetAddress addr : addrs) {
          if (!addr.isLoopbackAddress()) {
            String sAddr = addr.getHostAddress();
            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
            boolean isIPv4 = sAddr.indexOf(':') < 0;

            if (useIPv4) {
              if (isIPv4) return sAddr;
            } else {
              if (!isIPv4) {
                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
              }
            }
          }
        }
      }
    } catch (Exception ex) {
    } // for now eat exceptions
    return "";
  }
}
