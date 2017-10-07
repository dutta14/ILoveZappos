package anindya.ilovezappos;

import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import anindya.ilovezappos.databinding.LayoutDetailsBinding;
import anindya.ilovezappos.model.Result;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.layout_details);
        final Result result = (Result) getIntent().getExtras().get("mResult");
        binding.setResult(result);

        ImageView v = (ImageView) findViewById(R.id.image);
        Picasso.with(getApplicationContext()).load(result.getThumbnailImageUrl()).into(v);

        TextView originalPrice = (TextView) findViewById(R.id.originalPrice);
        originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        final FloatingActionButton updateCart = (FloatingActionButton) findViewById(R.id.update_cart);
        updateCart.setImageResource(Cart.has(result)? R.drawable.remove_item: R.drawable.add_item);
        updateCart.setOnClickListener(v1 -> updateCart.setImageResource(Cart.update(result)? R.drawable.remove_item : R.drawable.add_item));
    }
}
