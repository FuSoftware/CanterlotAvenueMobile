package fr.fusoft.canterlotavenue.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.fusoft.canterlotavenue.data.User;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Florent on 18/12/2017.
 */

public class LoginClient {
    private static final String LOG_TAG = "LoginClient";
    private static final String URL_PONIVERSE_OAUTH = "https://poniverse.net/oauth/login";
    private static final String URL_PONIVERSE_LOGIN = "https://poniverse.net/login";
    private static final String URL_CANTERLOT_AVENUE_PONAUTH = "http://canterlotavenue.com/ponauth/";
    private static final String URL_CANTERLOT_AVENUE = "https://canterlotavenue.com";

    public interface LoginListener{
        void onLoginSuccess();
        void onLoginFailed(String error);
    }

    private NetworkController controller = null;
    private LoginListener mListener = null;

    public LoginClient(NetworkController c){
        this.controller = c;
    }

    public void setLoginListener(LoginListener listener){
        this.mListener = listener;
    }

    /**
     Logins the user within the Canterlot Avenue website
     @param user The Poniverse username
     @param pass The Poniverse password
     @return Poniverse's Token
     */
    public User login(String user, String pass){
        String user_json = getUser(getLoginHtml(user,pass));
        return new User(user_json);
    }

    public String getLoginHtml(String user, String pass){
        try{
            return loginRequest(user,pass,false).body().string();
        }catch(Exception e){
            return "";
        }

    }

    /**
     Sends the login form to Poniverse
     @param user The Poniverse username
     @param pass The Poniverse password
     @param remember Remember the password
     @return Returned HTML
     */
    public Response loginRequest(String user, String pass, Boolean remember){
        return this.controller.sendRequest(generateLoginForm(user,pass,remember));
    }

    private Request generateLoginForm(String user, String pass, Boolean remember){
        RequestBody body =  new FormBody.Builder()
                .addEncoded("username", user)
                .addEncoded("password", pass)
                .addEncoded("submit","")
                .addEncoded("_token",getTokenJs())
                .build();

        Request request = new Request.Builder()
                .url(URL_PONIVERSE_LOGIN)
                .addHeader("content-type","application/x-www-form-urlencoded")
                .post(body)
                .build();
        return request;
    }

    private Request generateLoginJson(String user, String pass, Boolean remember){
        JSONObject data = new JSONObject();
        try{
            data.put("username",user);
            data.put("password",pass);
            data.put("submit","");
            data.put("_token",getTokenJs());

            RequestBody body =  RequestBody.create(MediaType.parse("application/json; charset=utf-8"),data.toString());

            Request request = new Request.Builder()
                    .url(URL_PONIVERSE_LOGIN)
                    .post(body)
                    .build();
            return request;

        }catch(Exception e){
            Log.w(LOG_TAG,"Error while generating login request : " + e.getMessage());
            return null;
        }
    }

    public String getPoniverseOauth(){
        Request request = new Request.Builder()
                .url(URL_CANTERLOT_AVENUE_PONAUTH)
                .get()
                .build();

        Response res = this.controller.sendRequest(request);
        String html = "";
        try{
            html = res.body().string();
        }catch(Exception e){
            Log.e(LOG_TAG,"Error while loading the Ponauth response body.");
        }
        Pattern p = Pattern.compile("window\\.location\\.replace\\(\"(.+)\"\\);");
        Matcher m = p.matcher(html);

        return m.find() ? m.group(1) : "";
    }

    public void loginPonauth(){
        String poniverse_oauth = getPoniverseOauth();

        Request request = new Request.Builder()
                .url(poniverse_oauth)
                .get()
                .build();

        Response res = this.controller.sendRequest(request);
    }

    public boolean isLoggedIn(){
        String html = this.controller.get(URL_CANTERLOT_AVENUE);
        Elements e = Jsoup.parse(html).body().getElementsByClass("logout");
        return e.size()>0;
    }

    /**
     Returns the current Poniverse identification token (not working)
     @return Poniverse's Token
     */
    public String getToken(){
        String html = this.controller.get(URL_PONIVERSE_OAUTH);
        Element form = Jsoup.parse(html).select("form").first();
        Element token = form.getElementsByAttributeValue("name","_token").first();
        return token.attr("value");
    }

    /**
     Returns the current Poniverse identification token
     @return Poniverse's Token
     */
    public String getTokenJs(){
        String html = this.controller.get(URL_PONIVERSE_LOGIN);
        Pattern p = Pattern.compile("token: \"(.+)\"");
        Matcher m = p.matcher(html);
        return m.find() ? m.group(1) : "";
    }

    public String getUser(String html){
        Pattern p = Pattern.compile("poniverse\\.user = (.+);");
        Matcher m = p.matcher(html);

        return m.find() ? m.group(1) : "";
    }

    public static class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private LoginListener listener;
        private LoginClient client;
        private String user;
        private String pass;

        public LoginTask(String user, String pass, NetworkController controller, LoginListener listener){
            this.listener = listener;
            this.client = new LoginClient(controller);
            this.user = user;
            this.pass = pass;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Response s = this.client.loginRequest(user,pass, false);
            this.client.loginPonauth();
            return this.client.isLoggedIn();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                this.listener.onLoginSuccess();
            }else{
                this.listener.onLoginFailed("Error while login to Poniverse");
            }
        }
    }

}
