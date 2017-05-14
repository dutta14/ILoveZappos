package anindya.ilovezappos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywordView.setText("");
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordView.getText().toString();
                Intent intent = new Intent(getApplicationContext(), Results.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            }
        });
    }
}
