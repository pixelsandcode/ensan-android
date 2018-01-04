package ir.app.ensan.model.network.service;

import ir.app.ensan.model.network.ApiConstance;
import ir.app.ensan.model.network.response.AddDeviceResponse;
import ir.app.ensan.model.network.response.LoginResponse;
import ir.app.ensan.model.network.response.SignUpResponse;
import ir.app.ensan.model.network.response.VerificationResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by k.monem on 6/5/2016.
 */
public interface UserService {

  @FormUrlEncoded @POST(ApiConstance.SIGNUP_API) Call<SignUpResponse> signUp(
      @Field("name") String name, @Field("mobile") String mobile);

  @FormUrlEncoded @POST(ApiConstance.LOGIN_API) Call<LoginResponse> login(
      @Field("auth") String auth, @Field("mobile") String mobile);

  @FormUrlEncoded @POST(ApiConstance.LOGIN_API) Call<LoginResponse> login(
      @Field("mobile") String mobile);

  @FormUrlEncoded @POST(ApiConstance.VERIFY_API) Call<VerificationResponse> verify(
      @Field("pin") String pin, @Field("mobile") String mobile);

  @FormUrlEncoded @POST(ApiConstance.ADD_DEVICE_API) Call<AddDeviceResponse> addDevice(
      @Header("Authorization") String authorization, @Field("token") String token);
}
