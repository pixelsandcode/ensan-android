package ir.app.ensan.model.network.callback;

import ir.app.ensan.model.network.response.BaseResponse;
import ir.app.ensan.util.NetworkUtil;
import java.net.SocketTimeoutException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by k.monem on 1/18/2017.
 */

public abstract class RegisterCallback<T> implements Callback<T> {

  private BaseResponse baseResponse;

  @Override public void onResponse(Call<T> call, Response<T> response) {

    if (response == null) {
      onNullResponse(call);
      return;
    }

    if (!response.isSuccessful()) {

      baseResponse = NetworkUtil.parseError(response.errorBody());

      if (baseResponse.getStatusCode() == 409){
        onRegisterFailed(call,response);
        return;
      }
      onRequestFail(call, response);
      //switch (baseResponse.getStatusCode()) {
      //
      //    case 13: //card data invalid
      //        onAuthenticationReject(call, response);
      //        return;
      //    case 33: //invalid password
      //        onAuthenticationReject(call, response);
      //        return;
      //    case 67: // session expired
      //        onSessionIdExpired(call, response);
      //        return;
      //    case 150://password must change
      //        onPasswordExpired(call, response);
      //        return;
      //    case 151: // username and phone number not match
      //        onAuthenticationReject(call, response);
      //        return;
      //    case 1000: // UnHandled exception
      //        onRequestFail(call, response);
      //        return;
      //    default: // other errors
      //        onRequestFail(call, response);
      //        return;
      //}
    }

    baseResponse = (BaseResponse) response.body();
    onRequestSuccess(call, response);
  }

  @Override public void onFailure(Call<T> call, Throwable t) {

    if (t instanceof SocketTimeoutException) {

    } else {

    }

    onRequestTimeOut(call, t);
  }

  public abstract void onRequestSuccess(Call<T> call, Response<T> response);

  public abstract void onRequestFail(Call<T> call, Response<T> response);

  public abstract void onRegisterFailed(Call<T> call, Response<T> response);

  public abstract void onRequestTimeOut(Call<T> call, Throwable t);

  public abstract void onNullResponse(Call<T> call);
}
