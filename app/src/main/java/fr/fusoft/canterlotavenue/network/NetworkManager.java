package fr.fusoft.canterlotavenue.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florent on 22/12/2017.
 */

public abstract class NetworkManager {

    public enum Method{
        GET("GET"),
        POST("POST"),
        HEAD("HEAD");

        private String label;

        Method(String label){
            this.label = label;
        }
    }

    public class Response{
        private String html;
        private int code;
        private Map<String, String> headers;
        private Request request;
        private boolean success = false;
        private String error;

        public Response(String html, int code, Map<String, String> headers){
            setCode(code);
            setHeaders(headers);
            setHtml(html);
            this.success = true;
        }

        public Response(int code, String error){
            setCode(code);
            setError(error);
            this.success = false;
        }

        public boolean isSuccess(){return this.success;}

        public void setError(String error){
            this.error = error;
        }

        public String getError(){
            return this.error;
        }

        public void setRequest(Request req){
            this.request = req;
        }

        public void setCode(int code){
            this.code = code;
        }

        public void setHtml(String html){
            this.html = html;
        }

        public void setHeaders(Map<String, String> headers){
            this.headers = headers;
        }

        public Request getRequest(){
            return this.request;
        }

        public String getHeader(String key){
            return this.headers.get(key);
        }

        public String getHtml(){
            return this.html;
        }

        public int getCode(){
            return this.code;
        }
    }

    public class Request{
        public class Builder{
            Request request = new Request();

            public Builder(){}

            public Request build(){return this.request;}

            public void url(String url){request.setUrl(url);}
            public void method(Method method){request.setMethod(method);}
            public void get(){request.setMethod(Method.GET);}
            public void post(){request.setMethod(Method.POST);}
            public void post(Map<String, String> form){request.setMethod(Method.POST);setForm(form);}
            public void form(Map<String, String> form){request.setForm(form);}
        }

        private Method method;
        private String url;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> form = new HashMap<>();

        public Request(){}

        public Request(Method method, String url){
            setUrl(url);
            setMethod(method);
        }

        public Request(Method method,  String url, Map<String, String> form){
            setUrl(url);
            setForm(form);
            setMethod(method);
        }

        public Request(Method method,  String url, Map<String, String> form, Map<String, String> headers){
            setUrl(url);
            setHeaders(headers);
            setForm(form);
            setMethod(method);
        }

        public void setMethod(Method m){
            this.method = m;
        }

        public Method getMethod(){
            return this.method;
        }

        public void setUrl(String url){
            this.url = url;
        }

        public void addHeader(String header, String value){
            this.headers.put(header,value);
        }

        public void setHeaders(Map<String, String> headers){
            this.headers = headers;
        }

        public void addFormField(String field, String value){
            this.form.put(field,value);
        }

        public void setForm(Map<String, String> form){
            this.form = form;
        }

        public String getFormField(String key){
            return this.getForm().get(key);
        }

        public String getUrl(){
            return this.url;
        }

        public Map<String, String> getForm(){
            return this.form;
        }

        public Map<String, String> getHeaders(){
            return this.headers;
        }
    }

    public NetworkManager(){

    }

    public Response get(String url){
        return this.processRequest(new Request(Method.GET,  url));
    }

    public Response post(String url, Map<String, String> parameters){return this.processRequest(new Request(Method.POST, url , parameters));}

    public abstract Response processRequest(Request r);
}
