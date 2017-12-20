package fr.fusoft.canterlotavenue.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import fr.fusoft.canterlotavenue.R;
import fr.fusoft.canterlotavenue.controller.LoginClient;
import fr.fusoft.canterlotavenue.controller.NetworkController;
import fr.fusoft.canterlotavenue.data.User;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Florent on 18/12/2017.
 */

public class TestActivity extends AppCompatActivity {

    final NetworkController c = new NetworkController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_debug_interface);

        Button b = findViewById(R.id.buttonLogin);
        final TextView t = findViewById(R.id.textViewHtml);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginOperation(TestActivity.this,TestActivity.this.c,  t).execute();
            }
        });
    }

    private static class LoginOperation extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView htmlViewer;
        private NetworkController controller;

        public LoginOperation(Context context, NetworkController c, TextView htmlViewer){
            this.context = context;
            this.htmlViewer = htmlViewer;
            this.controller = c;
        }

        @Override
        protected String doInBackground(Void... params) {
            //LoginClient l = new LoginClient(controller);
            //Response s = l.loginRequest(userpass, false);
            //l.loginPonauth();
            return"";
        }

        @Override
        protected void onPostExecute(String result) {
            String fixed = Jsoup.parse(result).body().html();
            htmlViewer.setText(Html.fromHtml(fixed));
            //Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }

}
