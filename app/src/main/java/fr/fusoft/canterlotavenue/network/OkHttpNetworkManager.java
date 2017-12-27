package fr.fusoft.canterlotavenue.network;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by fuguet on 23/12/17.
 */

public class OkHttpNetworkManager extends NetworkManager {
    private static final String LOG_TAG = "OkHttpNetworkManager";
    OkHttpClient client = null;

    /* This interceptor adds a custom User-Agent. */
    public class UserAgentInterceptor implements Interceptor {

        private final String userAgent;

        public UserAgentInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Request originalRequest = chain.request();
            okhttp3.Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("user-agent", userAgent)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }

    public OkHttpNetworkManager(){
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
                .followRedirects(false)
                .addInterceptor(new OkHttpNetworkManager.UserAgentInterceptor("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/56.0.2924.75 Mobile/14E5239e Safari/602.1"))
                .build();
    }

    @Override
    public Response process(Request req) {

        if(req == null){
            Log.e(LOG_TAG, "Trying to process a null Request");
            return null;
        }

        if(req.getUrl().equals("")){
            Log.e(LOG_TAG, "Empty URL to process");
            return null;
        }

        okhttp3.Request.Builder request = new okhttp3.Request.Builder();
        request.url(req.getUrl());

        switch(req.getMethod()){
            case GET:
                request.get();
                break;

            case POST:
                if(req.getForm().size() > 0){
                    FormBody.Builder body = new FormBody.Builder();
                    for(String key : req.getForm().keySet()){body.add(key, req.getFormField(key));}
                    request.post(body.build());
                }
                break;

            case HEAD:
                break;
        }

        try{
            okhttp3.Response oresp = client.newCall(request.build()).execute();

            Map<String, String> headers = new HashMap<>();

            for(String h : oresp.headers().names()){
                headers.put(h, oresp.header(h));
            }

            Response response = new Response(oresp.body().string(), oresp.code(), headers);
            response.setRequest(req);
            return response;

        }catch(Exception e){
            Response response = new Response(-1, "Error while processing the request : " + e.toString());
            response.setRequest(req);
            return response;
        }
    }
}
