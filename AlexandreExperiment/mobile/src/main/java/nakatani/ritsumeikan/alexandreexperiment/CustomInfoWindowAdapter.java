package nakatani.ritsumeikan.alexandreexperiment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import nakatani.ritsumeikan.alexandreexperiment.R;
import nakatani.ritsumeikan.alexandreexperiment.models.CustomLatLngs;
import nakatani.ritsumeikan.alexandreexperiment.models.Event;

class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{

    private Context mContext;
    private Marker mMarker;
    private ImageView mImageView;
    // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
    // "title" and "snippet".
    private final View mWindow;
    private boolean isFirst = true;
    private final View mContents;
    private ArrayList<Event> mEvents;
    private Bitmap mBitmap;

    CustomInfoWindowAdapter(Context context, ArrayList<Event> events)
    {

        mEvents = events;
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindow = inflater.inflate(R.layout.custom_info_window, null);
        mContents = inflater.inflate(R.layout.custom_info_contents, null);
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
//        if (mImageView != null) mImageView.setImageBitmap(null);
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        return null;
    }

    private void render(final Marker marker, View view)
    {
        String path_picto = "";
        if (mEvents == null) return;
        final ImageView imageView = ((ImageView) view.findViewById(R.id.badge));
        imageView.setImageBitmap(null);
        for (Event event : mEvents)
        {
            CustomLatLngs customLatLngs = event.getGeoJson().getLatLng().get(0);
            LatLng latLng = new LatLng(customLatLngs.getLatitude(), customLatLngs.getLongitude());
            if (marker.getPosition().equals(latLng))
            {
                path_picto = event.getPictograms().get(0).getPath();

                break;
            }
        }

//        String url = mContext.getResources().getString(R.string.server_url) + path_picto;
        if (mMarker != null && !marker.getPosition().equals(mMarker.getPosition())) isFirst = true;
       /* if (isFirst)
        {
            ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>()
            {
                @Override
                public void onResponse(Bitmap bitmap)
                {
                    mBitmap = bitmap;
                    marker.showInfoWindow();
                    mMarker = marker;
                    isFirst = false;
                }
            }, 0, 0, null, new Response.ErrorListener()
            {
                public void onErrorResponse(VolleyError error)
                {
                    imageView.setImageResource(R.drawable.fire_fighter);
                }
            });
        } else imageView.setImageBitmap(mBitmap);
*/

        String title = "";
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        // Spannable string allows us to edit the formatting of the text.
        SpannableString titleText = new SpannableString(title);
        titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
        titleUi.setText(titleText);

       /* String snippet = "";
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet.length() > 12)
        {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
            snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
            snippetUi.setText(snippetText);
        } else
        {
            snippetUi.setText("");
        }*/
    }
}
