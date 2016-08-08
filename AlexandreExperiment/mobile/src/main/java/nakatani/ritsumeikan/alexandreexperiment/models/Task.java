
package nakatani.ritsumeikan.alexandreexperiment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("_id")
    @Expose
    private nakatani.ritsumeikan.alexandreexperiment.models.Id Id;

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The Id
     */
    public nakatani.ritsumeikan.alexandreexperiment.models.Id getId() {
        return Id;
    }

    /**
     * 
     * @param Id
     *     The _id
     */
    public void setId(nakatani.ritsumeikan.alexandreexperiment.models.Id Id) {
        this.Id = Id;
    }

}
