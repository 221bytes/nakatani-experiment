package nakatani.ritsumeikan.alexandreexperiment.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

public class Event implements ClusterItem
{
    @SerializedName("_id")
    @Expose
    private nakatani.ritsumeikan.alexandreexperiment.models.Id Id;
    @SerializedName("last_update")
    @Expose
    private String lastUpdate;
    @SerializedName("pictograms")
    @Expose
    private ArrayList<Pictogram> mPictograms;
    @SerializedName("sounds")
    @Expose
    private ArrayList<Sound> mSounds;
    @SerializedName("geojson")
    @Expose
    private GeoJson mGeoJson;
    @SerializedName("importance")
    @Expose
    private int mImportance;


    /**
     * @return The Id
     */
    public nakatani.ritsumeikan.alexandreexperiment.models.Id getId()
    {
        return Id;
    }

    /**
     * @param Id The _id
     */
    public void setId(nakatani.ritsumeikan.alexandreexperiment.models.Id Id)
    {
        this.Id = Id;
    }

    /**
     * @return The lastUpdate
     */
    public String getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * @param lastUpdate The last_update
     */
    public void setLastUpdate(String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return List of Pictograms
     */

    public ArrayList<Pictogram> getPictograms()
    {
        return mPictograms;
    }
    /**
     *
     * @param pictograms
     *     The pictograms
     */
    public void setPictograms(ArrayList<Pictogram> pictograms)
    {
        mPictograms = pictograms;
    }

    /**
     * @return List of Sounds
     */
    public ArrayList<Sound> getSounds()
    {
        return mSounds;
    }

    /**
     * @param sounds The sounds
     */
    public void setSounds(ArrayList<Sound> sounds)
    {
        mSounds = sounds;
    }

    /**
     * @return Geojson
     */
    public GeoJson getGeoJson()
    {
        return mGeoJson;
    }

    /**
     * @param geoJson
     */
    public void setGeoJson(GeoJson geoJson)
    {
        mGeoJson = geoJson;
    }

    /**
     * @return Importance
     */
    public int getImportance()
    {
        return mImportance;
    }

    /**
     * @param importance
     */
    public void setImportance(int importance)
    {
        mImportance = importance;
    }

    @Override
    public LatLng getPosition()
    {
        return (mGeoJson.getClassicLatLng());
    }
}