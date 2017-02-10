package anindya.ilovezappos;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import anindya.ilovezappos.databinding.ActivityHomeBinding;

import static android.view.View.*;

public class Home extends ActionBarActivity {

    //Views for the activity.
    private ActivityHomeBinding binding; //used for databinding
    private FloatingActionButton mCart; //cart button
    private CoordinatorLayout layout;
    private Context mContext;
    private SearchView sView;  //search view
    private TextView noResult; //view to display no matching results
    private LinearLayout results; //view displaying results
    private Snackbar mSnackbar;
    private String mQuery;  //user-inputted string
    private MenuItem share, cart;
    private Product mProduct; //current product being viewed.

    //public URI to share between friends.
    private final String URI = "http://anindya.ilovezappos/";

    //Constants required to retrieve JSON data.
    private static final String BRAND_NAME = "brandName";
    private static final String IMAGE_URL = "thumbnailImageUrl";
    private static final String PRODUCT_ID = "productId";
    private static final String ORIGINAL_PRICE = "originalPrice";
    private static final String STYLE_ID = "styleId";
    private static final String COLOR_ID = "colorId";
    private static final String PRICE = "price";
    private static final String PERCENT_OFF = "percentOff";
    private static final String PRODUCT_URL = "productUrl";
    private static final String PRODUCT_NAME = "productName";

    private static LinkedHashMap<String,Product> mCartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        handleIntent(getIntent());
        mContext = getApplicationContext();

        noResult = ((TextView) findViewById(R.id.message));
        results = (LinearLayout) findViewById(R.id.details);

        layout = (CoordinatorLayout)findViewById(R.id.main_content);

        Uri uri = getIntent().getData();
        if(uri!=null)
            performSearch(getIntent());
        setupCartButton(); //set up floating action button.
        mCartItems = new LinkedHashMap<>();
        setupMoreInfo(); //set up collapsible view.
        TextView price = (TextView) findViewById(R.id.original_price);
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //in case of discount, strike through original price.
    }

    private void setupMoreInfo() {
        final TextView moreinfo = (TextView) findViewById(R.id.moreinfo);
        moreinfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout infobox = (LinearLayout)findViewById(R.id.infobox);
                infobox.setVisibility(View.VISIBLE);
                moreinfo.setVisibility(View.GONE);
            }
        });

        final TextView lessinfo = (TextView) findViewById(R.id.lessinfo);
        lessinfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout infobox = (LinearLayout)findViewById(R.id.infobox);
                infobox.setVisibility(View.GONE);
                moreinfo.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupCartButton() {
        mCart = (FloatingActionButton) findViewById(R.id.addtocart);
        mCart.hide();
        mCart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink);
                view.startAnimation(anim);
                int message;
                if(!mCartItems.containsKey(mQuery)) {
                    mCart.setImageResource(R.drawable.added);
                    message = R.string.added_to_cart;
                    mCartItems.put(mQuery,mProduct); //add product to cart.
                }
                else {
                    mProduct = mCartItems.remove(mQuery); //remove product from cart.
                    mCart.setImageResource(R.drawable.addtocart);
                    message = R.string.removed_from_cart;
                }
                Snackbar.make(layout, mContext.getText(message), Snackbar.LENGTH_SHORT).setAction("Undo",new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mCartItems.containsKey(mQuery))
                            mProduct = mCartItems.remove(mQuery);
                        else
                            mCartItems.put(mQuery, mProduct);
                        mCart.setImageResource(mCartItems.containsKey(mQuery)?R.drawable.added:R.drawable.addtocart);
                    }
                }).show();
                updateTotal();
                if(Cart.getAdapter() != null)
                    Cart.getAdapter().notifyDataSetChanged(); //update cart activity.
            }
        });
    }

    void updateTotal() {
        Product total;
        String key = "total_value_in_cart";
        if(mCartItems.containsKey(key)) {
            total = mCartItems.get(key);
            mCartItems.remove(key);
        }
        else
            total = new Product("<b><u>TOTAL</b></u>", 0);

        if(mCartItems.containsKey(mQuery))
            total.realPrice+= mProduct.realPrice;
        else
            total.realPrice-= mProduct.realPrice;
        total.realPrice = (total.realPrice*100.00)/100.00;
        total.setPrice(total.realPrice);
        mCartItems.put(key,total);

        if(mCartItems.size() == 1)
            mCartItems.clear();
    }

    protected void onResume() {
        super.onResume();
        if(mCart != null)
            mCart.setImageResource(mCartItems.containsKey(mQuery)?R.drawable.added:R.drawable.addtocart);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            CoordinatorLayout layout = (CoordinatorLayout)findViewById(R.id.main_content);
            mSnackbar = Snackbar.make(layout, String.format(mContext.getString(R.string.loading), mQuery), Snackbar.LENGTH_INDEFINITE);
            mSnackbar.show();

            results.setAlpha(0.2f);
            sView.onActionViewCollapsed();
            search(mQuery);
        }
    }

    void search(String query) {
        new JsonReceiver(new JsonReceiver.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                parseJSON(output);
            }
        }).execute(query);
    }

    private void parseJSON(String output) {
        try {
            JSONObject obj = new JSONObject(output);
            int currentCount = Integer.parseInt(obj.getString("currentResultCount"));
            if(mSnackbar !=null)
                mSnackbar.dismiss();
            if(currentCount == 0) {
                noResult.setText(R.string.no_match);
                noResult.setVisibility(VISIBLE);
                results.setVisibility(GONE);
                mCart.hide();
                share.setVisible(false);
            } else {
                share.setVisible(true);
                cart.setVisible(true);
                JSONArray resultArray = obj.getJSONArray("results");
                JSONObject result = resultArray.getJSONObject(0);

                results.setVisibility(VISIBLE);
                results.setAlpha(1f);
                noResult.setVisibility(GONE);
                mCart.show();
                mCart.setImageResource(mCartItems.containsKey(mQuery)?R.drawable.added:R.drawable.addtocart);

                mProduct = new Product(result.getString(BRAND_NAME), null, result.getString(PRODUCT_ID),
                                            result.getString(ORIGINAL_PRICE), result.getString(STYLE_ID), result.getString(COLOR_ID),
                                            result.getString(PRICE), result.getString(PERCENT_OFF),  result.getString(PRODUCT_URL), result.getString(PRODUCT_NAME));

                new ImageLoader(new ImageLoader.ImageResponse() {

                    @Override
                    public void processFinish(Bitmap op) {
                        mProduct.setImage(new BitmapDrawable(op));
                        binding.setProduct(mProduct);
                    }
                }).execute(result.getString(IMAGE_URL));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        performSearch(intent);
        handleIntent(intent);
    }

    void performSearch(Intent intent) {
        Uri data = intent.getData();
        if(data!=null) {
            String text = data.toString();
            mQuery = text.substring(text.lastIndexOf("/")+1);
            search(mQuery);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);

        SearchManager sMgr = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sView = (SearchView) (menu.findItem(R.id.search)).getActionView();
        sView.setSearchableInfo(sMgr.getSearchableInfo(getComponentName()));

        sView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemsVisibility(menu, searchItem, false);
            }
        });
        sView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setItemsVisibility(menu, searchItem, true);
                return false;
            }
        });

        share = menu.findItem(R.id.menu_item_share);
        share.setVisible(false);
        cart = menu.findItem(R.id.cart);
        cart.setVisible(false);
        return true;
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) {
                if((visible && noResult.getVisibility()==View.GONE) || (!visible))
                item.setVisible(visible);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                String message = getString(R.string.share_intro);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Zappos");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message + URI + mQuery);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.search:
                return true;
            case R.id.cart:
                Intent i = new Intent();
                i.setClass(mContext, Cart.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    static HashMap<String,Product> getMCartItems() {
        return mCartItems;
    }
}
