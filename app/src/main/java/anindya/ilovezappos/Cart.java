package anindya.ilovezappos;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cart extends ActionBarActivity {

    private List<Product> productList = new ArrayList<>();
    private RecyclerView recyclerView;
    private static ProductAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(productList.size()>0) {
                    Home.getMCartItems().clear();
                    productList.clear();
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(view, R.string.items_deleted, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, R.string.cart_empty, Snackbar.LENGTH_LONG).show();
                }
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ProductAdapter(productList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareData();
        mAdapter.notifyDataSetChanged();
    }

    void prepareData() {
        HashMap<String,Product> p = Home.getMCartItems();
        for(String i: p.keySet()) {
            productList.add(p.get(i));
        }
    }


    public static ProductAdapter getAdapter() {
        return mAdapter;
    }
}


class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private List<Product> productList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, price;

        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
            price = (TextView) view.findViewById(R.id.price);
        }
    }

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.itemName.setText(p.productName);
        holder.price.setText(p.price);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}