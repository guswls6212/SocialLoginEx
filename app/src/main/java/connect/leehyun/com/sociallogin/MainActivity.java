package connect.leehyun.com.sociallogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int socialLoginResult = 0;
    private TextView resultText;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultText = findViewById(R.id.login_result);
        //Setting Logout Button
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        Intent intent = getIntent();
        if(intent != null){
            socialLoginResult = intent.getExtras().getInt("social");
        }

        loginResult();
    }

    public void loginResult(){
        switch (socialLoginResult){
            case LoginActivity.GOOGLE_SIGN_IN:
                resultText.setText("Google Login Success");
                break;
            case LoginActivity.FACEBOOK_SIGN_IN:
                resultText.setText("FACEBOOK Login Success");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_out_button:
                switch (socialLoginResult){
                    case LoginActivity.GOOGLE_SIGN_IN:
                        setResult(LoginActivity.GOOGLE_SIGN_IN);
                        finish();
                        break;
                    case LoginActivity.FACEBOOK_SIGN_IN:
                        setResult(LoginActivity.FACEBOOK_SIGN_IN);
                        finish();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
