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

    public static class Response{
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

    public static class Request {
        public static class Builder {
            Request request = new Request();

            public Builder() {
            }

            public Request build() {
                return this.request;
            }

            public Builder header(String header, String value) {
                request.addHeader(header, value);
                return this;
            }

            public Builder url(String url) {
                request.setUrl(url);
                return this;
            }

            public Builder method(Method method) {
                request.setMethod(method);
                return this;
            }

            public Builder get() {
                request.setMethod(Method.GET);
                return this;
            }

            public Builder post() {
                request.setMethod(Method.POST);
                return this;
            }

            public Builder post(Map<String, String> form) {
                request.setMethod(Method.POST);
                form(form);
                return this;
            }

            public Builder form(Map<String, String> form) {
                request.setForm(form);
                return this;
            }
        }

        private Method method;
        private String url;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> form = new HashMap<>();

        public Request() {
        }

        public Request(Method method, String url) {
            setUrl(url);
            setMethod(method);
        }

        public Request(Method method, String url, Map<String, String> form) {
            setUrl(url);
            setForm(form);
            setMethod(method);
        }

        public Request(Method method, String url, Map<String, String> form, Map<String, String> headers) {
            setUrl(url);
            setHeaders(headers);
            setForm(form);
            setMethod(method);
        }

        public void setMethod(Method m) {
            this.method = m;
        }

        public Method getMethod() {
            return this.method;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void addHeader(String header, String value) {
            this.headers.put(header, value);
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public void addFormField(String field, String value) {
            this.form.put(field, value);
        }

        public void setForm(Map<String, String> form) {
            this.form = form;
        }

        public String getFormField(String key) {
            return this.getForm().get(key);
        }

        public String getUrl() {
            return this.url;
        }

        public Map<String, String> getForm() {
            return this.form;
        }

        public Map<String, String> getHeaders() {
            return this.headers;
        }
    }

    public NetworkManager(){

    }

    public Response get(String url){
        return this.process(new Request(Method.GET,  url));
    }

    public Response post(String url, Map<String, String> parameters){return this.process(new Request(Method.POST, url , parameters));}

    public abstract Response process(Request r);
}
