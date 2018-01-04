package ir.app.ensan.model.network.service;

import ir.app.ensan.model.network.ApiConstance;
import ir.app.ensan.model.network.request.NotifyRequest;
import ir.app.ensan.model.network.response.AddGuardianResponse;
import ir.app.ensan.model.network.response.GuardianListResponse;
import ir.app.ensan.model.network.response.NotifyResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Khashayar on 03/01/2018.
 */

public interface GuardianService {

  @FormUrlEncoded @POST(ApiConstance.ADD_GUARDIAN_API) Call<AddGuardianResponse> addGuardian(
      @Header("Authorization") String authorization, @Field("name") String name,
      @Field("mobile") String mobile);

  @POST(ApiConstance.NOTIFY_GUARDIAN_API) Call<NotifyResponse> notifyGuardians(
      @Header("Authorization") String authorization, @Body NotifyRequest notifyRequest);

  @GET(ApiConstance.GUARDIAN_LIST_API) Call<GuardianListResponse> getGuardianList(
      @Header("Authorization") String authorization);
}
