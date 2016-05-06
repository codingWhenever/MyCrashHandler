package dbh.leo.com.mycrashhandler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int count = 0;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
    }

    public void btnClick(View view) {
        if (count == 0) {
            count += 1;
            Toast.makeText(this, getResources().getString(R.string.str_click2), Toast.LENGTH_SHORT).show();
            btn.setText(getString(R.string.str_click3));
        } else if (count == 1) {
            Toast.makeText(this, getResources().getString(R.string.str_click4), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(MyCrashHandler.TAG + "--->这个是自己抛出的异常");
        }
    }
}
