package fr.fusoft.canterlotavenue.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.fusoft.canterlotavenue.R;
import fr.fusoft.canterlotavenue.controller.LoginClient;
import fr.fusoft.canterlotavenue.controller.NetworkController;
import fr.fusoft.canterlotavenue.network.NetworkManager;
import fr.fusoft.canterlotavenue.network.OkHttpNetworkManager;

/**
 * Created by Florent on 20/12/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private NetworkManager c = new OkHttpNetworkManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUser = findViewById(R.id.etUser);
        final EditText etPass = findViewById(R.id.etPass);

        Button b = findViewById(R.id.buttonLogin);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = etUser.getText().toString();
                String pass = etPass.getText().toString();

                new LoginClient.LoginTask(user, pass, c, new LoginClient.LoginListener() {
                    @Override
                    public void onLoginSuccess() {
                        Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoginFailed(String error) {
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        });
    }
}
