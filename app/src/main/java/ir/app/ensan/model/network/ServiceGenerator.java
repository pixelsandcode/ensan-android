package ir.app.ensan.model.network;

import ir.app.ensan.Config;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by k.monem on 6/5/2016.
 */
public class ServiceGenerator {

  private static ServiceGenerator instance;

  private Retrofit retrofit;
  private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
  private Retrofit.Builder builder;

  public ServiceGenerator() {
    init();
  }

  public static ServiceGenerator getInstance() {

    if (instance == null) {
      instance = new ServiceGenerator();
    }
    return instance;
  }

  private void init() {
    builder = new Retrofit.Builder().baseUrl(ApiConstance.HOST_URL)
        .addConverterFactory(GsonConverterFactory.create());

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(Config.HTTP_LOG_LEVEL);
    httpClient.addInterceptor(logging);

    httpClient.connectTimeout(20, TimeUnit.SECONDS);
    httpClient.readTimeout(5, TimeUnit.SECONDS);

    httpClient.addInterceptor(new Interceptor() {
      @Override public okhttp3.Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder();
        //
        //for (Map.Entry entry : NetworkUtil.getAuthorizationHeader().entrySet()) {
        //  requestBuilder.header(entry.getKey() + "", entry.getValue() + "");
        //}
        requestBuilder.method(original.method(), original.body());

        Request request = requestBuilder.build();
        return chain.proceed(request);
      }
    });

    OkHttpClient client = httpClient.build();
    retrofit = builder.client(client).build();
  }

  public <S> S createService(Class<S> serviceClass) {
    return retrofit.create(serviceClass);
  }

  public Retrofit getRetrofit() {
    return retrofit;
  }
}
