
package anindya.ilovezappos.model;

import android.text.Html;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Result implements Serializable {

    @SerializedName("brandName")
    @Expose
    private String brandName;
    @SerializedName("thumbnailImageUrl")
    @Expose
    private String thumbnailImageUrl;
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("originalPrice")
    @Expose
    private String originalPrice;
    @SerializedName("styleId")
    @Expose
    private String styleId;
    @SerializedName("colorId")
    @Expose
    private String colorId;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("percentOff")
    @Expose
    private String percentOff;
    @SerializedName("productUrl")
    @Expose
    private String productUrl;
    @SerializedName("productName")
    @Expose
    private String productName;

    public String getBrandName() {
        return brandName;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getStyleId() {
        return styleId;
    }

    public String getColorId() {
        return colorId;
    }

    public String getPrice() {
        return price;
    }

    public String getPercentOff() {
        return String.format("%s OFF", percentOff);
    }

    public String getProductUrl() {
        return productUrl;
    }

    public String getProductName() {
        return Html.fromHtml(productName).toString();
    }
}
