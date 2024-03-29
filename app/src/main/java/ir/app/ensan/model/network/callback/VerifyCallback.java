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

public abstract class VerifyCallback<T> implements Callback<T> {

  private BaseResponse baseResponse;

  @Override public void onResponse(Call<T> call, Response<T> response) {

    if (response == null) {
      onNullResponse(call);
      return;
    }

    if (!response.isSuccessful()) {

      baseResponse = NetworkUtil.parseError(response.errorBody());

      if (baseResponse.getStatusCode() == 403){
        onVerificationCodeInvalid(call,response);
        return;
      }
      onRequestFail(call, response);
      return;
    }

    baseResponse = (BaseResponse) response.body();
    NetworkRequestManager.getInstance().saveAuthorizationKey(response);
    onRequestSuccess(call, response);
  }

  @Override public void onFailure(Call<T> call, Throwable t) {

    if (t instanceof SocketTimeoutException) {
      onRequestTimeOut(call, t);
    } else {

    }


  }

  public abstract void onRequestSuccess(Call<T> call, Response<T> response);

  public abstract void onRequestFail(Call<T> call, Response<T> response);

  public abstract void onVerificationCodeInvalid(Call<T> call, Response<T> response);

  public abstract void onRequestTimeOut(Call<T> call, Throwable t);

  public abstract void onNullResponse(Call<T> call);
}
