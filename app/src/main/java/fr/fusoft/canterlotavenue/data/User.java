package fr.fusoft.canterlotavenue.data;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Florent on 18/12/2017.
 */

public class User {
    private static final String LOG_TAG = "user";

    public int id;
    public String username;
    public String display_name;
    public String slug;
    public String email;
    public String permissions;
    public boolean activated;
    public String activated_at;
    public String created_at;
    public String updated_at;
    public String email_hash;
    public String username_hash;

    private String json_data = "";

    public User(String json){
       this.loadFromJson(json);
    }

    public User(JSONObject o){
        this.loadFromJson(o);
    }

    public void loadFromJson(String json){
        try{
            this.loadFromJson(new JSONObject(json));
        }catch(Exception e){
            Log.w(LOG_TAG,"Failed to load User, " + e.toString());
        }

    }

    public void loadFromJson(JSONObject o){
        try{
            this.id = o.getInt("id");
            this.username = o.getString("username");
            this.display_name = o.getString("display_name");
            this.slug = o.getString("slug");
            this.email = o.getString("email");
            //this.permissions = o.getString("permissions");
            this.activated = o.getBoolean("activated");
            this.activated_at = o.getString("activated_at");
            this.created_at = o.getString("created_at");
            this.updated_at = o.getString("updated_at");
            this.email_hash = o.getString("email_hash");
            this.username_hash = o.getString("username_hash");

            this.json_data = o.toString();
        }catch(Exception e){
            Log.w(LOG_TAG,"Failed to load User from JSON, " + e.toString());
        }
    }
}
