package anindya.ilovezappos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import anindya.ilovezappos.model.Result;

public class ViewCart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        final Context mContext = getApplicationContext();
        Result[] output = Cart.getItems();
        final Results.MyAdapter adapter = new Results.MyAdapter(mContext, output);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Result result = adapter.getItem(position);
                Intent intent = new Intent(mContext, Details.class);
                intent.putExtra("mResult", result);
                startActivity(intent);
            }
        });

        findViewById(R.id.cart).setVisibility(View.GONE);
    }
}
