package anindya.ilovezappos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Cart.init();

        final EditText keywordView = (EditText) findViewById(R.id.keyword);
        Button search = (Button) findViewById(R.id.search);

        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(v -> keywordView.setText(""));

        search.setOnClickListener(v -> {
            String keyword = keywordView.getText().toString();
            Intent intent = new Intent(getApplicationContext(), Results.class);
            intent.putExtra("keyword", keyword);
            startActivity(intent);
        });
    }
}
