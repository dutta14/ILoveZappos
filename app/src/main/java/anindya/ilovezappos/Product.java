package anindya.ilovezappos;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by anind on 2/5/2017.
 */

public class Product {

    public Spanned brandName, productId, originalPrice, styleId, colorId, price, percentOff, productUrl, productName;
    public Drawable image;
    Product(String brandName, Bitmap image, String productId, String originalPrice, String styleId,
            String colorId, String price, String percentOff, String productUrl, String productName) {
        this.brandName = Html.fromHtml(brandName);
        this.image = new BitmapDrawable(image);
        this.productId = Html.fromHtml(productId);
        this.originalPrice = Html.fromHtml(originalPrice);
        this.styleId = Html.fromHtml(styleId);
        this.colorId = Html.fromHtml(colorId);
        this.price = Html.fromHtml(price);
        this.percentOff = Html.fromHtml(percentOff);
        this.productUrl = Html.fromHtml(productUrl);
        this.productName = Html.fromHtml(productName);
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}