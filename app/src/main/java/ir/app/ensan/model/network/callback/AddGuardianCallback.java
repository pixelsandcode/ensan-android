package ir.app.ensan.model.network.callback;

import ir.app.ensan.model.network.NetworkRequestManager;
import ir.app.ensan.model.network.response.BaseResponse;
import ir.app.ensan.util.NetworkUtil;
import java.net.SocketTimeoutException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by k.monem on 1/18/2017.
 */

public abstract class AddGuardianCallback<T> implements Callback<T> {

  private BaseResponse baseResponse;

  @Override public void onResponse(Call<T> call, Response<T> response) {

    if (response == null) {
      onNullResponse(call);
      return;
    }

    if (!response.isSuccessful()) {

      baseResponse = NetworkUtil.parseError(response.errorBody());

      if (baseResponse.getStatusCode() == 401){
        onTokenExpire(call, response);
        return;
      }

      if (baseResponse.getStatusCode() == 405){
        onSelfGuardianAdded(call, response);
        return;
      }

      if (baseResponse.getStatusCode() == 409){
        onGuardianAddBefore(call,response);
        return;
      }
      onRequestFail(call, response);
    }

    baseResponse = (BaseResponse) response.body();
    NetworkRequestManager.getInstance().saveAuthorizationKey(response);
    onRequestSuccess(call, response);
  }

  @Override public void onFailure(Call<T> call, Throwable t) {

    if (t instanceof SocketTimeoutException) {

    } else {

    }

    onRequestTimeOut(call, t);
  }

  public abstract void onRequestSuccess(Call<T> call, Response<T> response);

  public abstract void onGuardianAddBefore(Call<T> call, Response<T> response);

  public abstract void onRequestFail(Call<T> call, Response<T> response);

  public abstract void onSelfGuardianAdded(Call<T> call, Response<T> response);

  public abstract void onTokenExpire(Call<T> call, Response<T> response);

  public abstract void onRequestTimeOut(Call<T> call, Throwable t);

  public abstract void onNullResponse(Call<T> call);
}
