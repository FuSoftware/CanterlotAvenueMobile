package fr.fusoft.canterlotavenue.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florent on 22/12/2017.
 */

public abstract class NetworkManager {

    public class Response{
        private String body;
        private int code;
        private Map<String, String> headers;
    }

    public class Request{
        private String url;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> form = new HashMap<>();

        public Request(String url){
            setUrl(url);
        }

        public Request(String url, Map<String, String> form){
            setUrl(url);
            setForm(form);
        }

        public Request(String url, Map<String, String> form, Map<String, String> headers){
            setUrl(url);
            setHeaders(headers);
            setForm(form);
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
        return this.get(new Request(url));
    }
    public abstract Response get(Request req);

    public Response post(String url, Map<String, String> parameters){
        return this.post(new Request(url, parameters));
    }
    public abstract Response post(Request req);

    public abstract Response head(Request req);
}
