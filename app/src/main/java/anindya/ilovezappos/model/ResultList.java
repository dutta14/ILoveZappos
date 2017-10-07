package anindya.ilovezappos.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultList {

    @SerializedName("originalTerm")
    @Expose
    private String originalTerm;
    @SerializedName("currentResultCount")
    @Expose
    private String currentResultCount;
    @SerializedName("totalResultCount")
    @Expose
    private String totalResultCount;
    @SerializedName("term")
    @Expose
    private String term;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("statusCode")
    @Expose
    private String statusCode;

    public List<Result> getResults() {
        return results;
    }
}
