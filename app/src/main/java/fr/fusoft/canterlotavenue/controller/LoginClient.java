package fr.fusoft.canterlotavenue.controller;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.fusoft.canterlotavenue.data.User;
import fr.fusoft.canterlotavenue.network.NetworkManager;
import fr.fusoft.canterlotavenue.network.NetworkManager.Request;
import fr.fusoft.canterlotavenue.network.NetworkManager.Response;

/**
 * Created by Florent on 18/12/2017.
 */

public class     LoginClient {
    private static final String LOG_TAG = "LoginClient";
    private static final String URL_PONIVERSE_OAUTH = "https://poniverse.net/oauth/login";
    private static final String URL_PONIVERSE_LOGIN = "https://poniverse.net/login";
    private static final String URL_CANTERLOT_AVENUE_PONAUTH = "http://canterlotavenue.com/ponauth/";
    private static final String URL_CANTERLOT_AVENUE = "https://canterlotavenue.com";

    public interface LoginListener{
        void onLoginSuccess();
        void onLoginFailed(String error);
    }

    private NetworkManager controller = null;
    private LoginListener mListener = null;

    public LoginClient(NetworkManager c){
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
            return loginRequest(user,pass,false).getHtml();
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
    public NetworkManager.Response loginRequest(String user, String pass, Boolean remember){
        return this.controller.process(generateLoginForm(user,pass,remember));
    }

    private Request generateLoginForm(String user, String pass, Boolean remember){
        Map<String, String> form = new HashMap<>();
        form.put("username", user);
        form.put("password", pass);
        form.put("submit","");
        form.put("_token", getTokenJs());

        Request request = new Request.Builder()
                .url(URL_PONIVERSE_LOGIN)
                .form(form)
                .build();

        return request;
    }

    public String getPoniverseOauth(){
        Request request = new Request.Builder()
                .url(URL_CANTERLOT_AVENUE_PONAUTH)
                .get()
                .build();

        Response res = this.controller.process(request);
        String html = "";
        try{
            html = res.getHtml();
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

        Response res = this.controller.process(request);
    }

    public boolean isLoggedIn(){
        String html = this.controller.get(URL_CANTERLOT_AVENUE).getHtml();
        Elements e = Jsoup.parse(html).body().getElementsByClass("logout");
        return e.size()>0;
    }

    /**
     Returns the current Poniverse identification token (not working)
     @return Poniverse's Token
     */
    public String getToken(){
        String html = this.controller.get(URL_PONIVERSE_OAUTH).getHtml();
        Element form = Jsoup.parse(html).select("form").first();
        Element token = form.getElementsByAttributeValue("name","_token").first();
        return token.attr("value");
    }

    /**
     Returns the current Poniverse identification token
     @return Poniverse's Token
     */
    public String getTokenJs(){
        String html = this.controller.get(URL_PONIVERSE_LOGIN).getHtml();
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

        public LoginTask(String user, String pass, NetworkManager controller, LoginListener listener){
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
