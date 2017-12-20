package fr.fusoft.canterlotavenue.controller;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Florent on 18/12/2017.
 */

public class NetworkController {
    OkHttpClient client = null;
    private static final String LOG_TAG = "NetworkController";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final NetworkController globalController = new NetworkController();

    /* This interceptor adds a custom User-Agent. */
    public class UserAgentInterceptor implements Interceptor {

        private final String userAgent;

        public UserAgentInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("user-agent", userAgent)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }

    public NetworkController(){
        initOkHttp();
    }

    private void initOkHttp(){
        this.client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                    private HttpUrl getAuthority(HttpUrl url){
                        return new HttpUrl.Builder()
                                .scheme(url.scheme())
                                .host(url.host())
                                .build();
                    }

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(getAuthority(url), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(getAuthority(url));
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .followRedirects(true)
                //.addInterceptor(new StandardHeadersInterecptor())
                .addInterceptor(new UserAgentInterceptor("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/56.0.2924.75 Mobile/14E5239e Safari/602.1"))
                .build();
    }

    public String testPost(){
        String url = "http://httpbin.org/post";

        RequestBody b = new FormBody.Builder()
                .add("var1", "12")
                .add("var2", "13")
                .add("var3", "14")
                .build();
        return post(url,b);
    }

    public String testGet(){
        String url = "http://httpbin.org/get";
        return get(url);
    }

    public String get(String url){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return getResponseBody(request);
    }

    public String postJson(String url, JSONObject json){
        return this.postJson(url,json.toString());
    }

    public String postJson(String url, String json){
        RequestBody body = RequestBody.create(JSON, json);

        return this.post(url,body);
    }

    public String postForm(String url, Map<String, String> values) {
        FormBody.Builder form = new FormBody.Builder();

        for (int i = 0; i < values.size(); i++) {
            String key = (String) values.keySet().toArray()[i];
            form.add(key, values.get(key));
        }

        return this.post(url, form.build());
    }

    public String post(String url, RequestBody body){
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return getResponseBody(request);
    }

    public String getResponseBody(Request request){
        debugBody(request);

        try{
            Response response = sendRequest(request);
            Log.d(LOG_TAG, String.format("URL : %1$s gave response %2$d", request.url().toString(), response.code()));

            if(!response.isSuccessful())
                processError(response);

            String body = response.body().string();
            response.body().close();
            return body;
        }catch(Exception e){
            Log.e(LOG_TAG,"Exception while sending OkHTTP request : " + e.getMessage());
            return "";
        }
    }

    public Response sendRequest(Request request){
        try{
            return client.newCall(request).execute();
        }catch(Exception e){
            Log.e(LOG_TAG,"Exception while sending OkHTTP request : " + e.toString());
            return null;
        }

    }

    private void debugBody(Request request)
    {
        if(request.body() != null){
            FormBody b = (FormBody)request.body();

            for(int i=0;i<b.size();i++){
                Log.d(LOG_TAG, String.format("%1s : %2s",b.encodedName(i),b.encodedValue(i)));
            }
        }
    }

    private void processError(Response response){
        Log.e(LOG_TAG,String.format("Got error %1d with message %2s", response.code(), response.message()));

        Log.e(LOG_TAG,"Response Headers :");
        for(int i=0;i<response.headers().size();i++){
            String n = response.headers().name(i);
            String h = response.headers().get(n);
            Log.e(LOG_TAG, String.format("    Header %1s : %2s " ,n,h ));
        }

        Log.e(LOG_TAG,"Request Headers :");
        for(int i=0;i<response.request().headers().size();i++){
            String n = response.request().headers().name(i);
            String h = response.request().headers().get(n);
            Log.e(LOG_TAG, String.format("    Header %1s : %2s " ,n,h ));
        }
    }
}
