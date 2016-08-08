package nakatani.ritsumeikan.alexandreexperiment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import java.util.ArrayList;
import java.util.Collection;

import nakatani.ritsumeikan.alexandreexperiment.models.Event;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<Event>, ClusterManager.OnClusterInfoWindowClickListener<Event>, ClusterManager.OnClusterItemClickListener<Event>, ClusterManager.OnClusterItemInfoWindowClickListener<Event>
{

    private GoogleMap mMap;
    private ClusterManager<Event> mEventClusterManager;
    private CustomMarkerManager mCustomMarkerManager;
    private Marker mCurrentMarker;
    private MarkerOptions mMarkerOptions;
    private ArrayList<Event> mEvents = new ArrayList<>();
    private MarkerManager.Collection mMarkerCollection;
    private Context mContext;
    private Marker mMarkerToAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FloatingActionButton floatingActionButton = (FloatingActionButton) v;
            }
        });
        mMarkerOptions = new MarkerOptions().position(new LatLng(0, 0)).title("");
        mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp));
        mapFragment.getMapAsync(this);
        mContext = this;
    }

    private void moveCurrentMarker(LatLng latLng)
    {
        if (mCurrentMarker != null) mCurrentMarker.remove();
        mMarkerOptions.position(latLng);
        mCurrentMarker = mMap.addMarker(mMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            requestPerm();

        } else mMap.setMyLocationEnabled(true);


//        mMap.setOnMarkerClickListener(mOnMarkerClickListener);

        mEventClusterManager = new ClusterManager<Event>(this, mMap, new CustomMarkerManager(mMap));

        mCustomMarkerManager = new CustomMarkerManager(mMap);
        mEventClusterManager.setRenderer(new EventRenderer());
        mMap.setOnCameraChangeListener(mEventClusterManager);
        mMap.setOnMarkerClickListener(mCustomMarkerManager);
        mMarkerCollection = mCustomMarkerManager.newCollection();
//        mMap.setOnMarkerClickListener(mOnMarkerClickListener);

        // addHeatmap();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {


                moveCurrentMarker(latLng);


            }
        });

        CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(this, mEvents);
        mMap.setInfoWindowAdapter(customInfoWindowAdapter);


     /*   mGetRequestService.getEvent(new Callback<ArrayList<Event>>()
        {
            @Override
            public void success(ArrayList<Event> events, Response response)
            {
                mEvents = events;
                CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(getApplicationContext(), mEvents);
                mMap.setInfoWindowAdapter(customInfoWindowAdapter);
                for (Event event : events)
                {
                    switch (event.getGeoJson().getType())
                    {
                        case POINT:
                            addMarkerToMap(event);
                            break;
                        case POLYGON:
                            addPolygonToMap(event);
                            break;
                        case POLYLINE:
                            addPolylineToMap(event);
                            break;
                    }
                }


            }

            @Override
            public void failure(RetrofitError error)
            {
                RetrofitError retrofitError = error;
            }
        });*/

    }

    @Override
    public boolean onClusterClick(Cluster<Event> cluster)
    {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Event> cluster)
    {

    }

    @Override
    public boolean onClusterItemClick(Event event)
    {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Event event)
    {

    }


    private class EventRenderer extends DefaultClusterRenderer<Event>
    {
        //        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator;
        private final float mDensity;

        private ShapeDrawable mColoredCircleBackground;
        private SparseArray<BitmapDescriptor> mIcons = new SparseArray();
        private Context mContext;

//        private final ImageView mImageView;
//        private final ImageView mClusterImageView;
//        private final int mDimension;

        public EventRenderer()
        {
            super(getApplicationContext(), mMap, mEventClusterManager);
            this.mContext = getApplicationContext();
            this.mDensity = mContext.getResources().getDisplayMetrics().density;
            this.mClusterIconGenerator = new IconGenerator(mContext);
            this.mClusterIconGenerator.setContentView(this.makeSquareTextView());
            this.mClusterIconGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);
            this.mClusterIconGenerator.setBackground(this.makeClusterBackground());

//            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
//            mClusterIconGenerator.setContentView(multiProfile);
//            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
//
//            mImageView = new ImageView(getApplicationContext());
//            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
//            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
//            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
//            mImageView.setPadding(padding, padding, padding, padding);
//            mIconGenerator.setContentView(mImageView);
        }

        @Override
        public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<Event> listener)
        {

        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Event> cluster, MarkerOptions markerOptions)
        {
            // Main color
            Collection<Event> events = cluster.getItems();
            int[] ints = new int[4];
            for (Event event : events)
            {
                switch (event.getImportance())
                {
                    case 0:
                        ints[0]++;
                        break;
                    case 1:
                        ints[1]++;
                        break;
                    case 2:
                        ints[2]++;
                        break;
                    case 3:
                        ints[3]++;
                        break;
                    default:
                        break;
                }
            }
            int clusterColor = mContext.getResources().getColor(getbackground(ints));
            int bucket = this.getBucket(cluster);
            BitmapDescriptor descriptor = this.mIcons.get(bucket);
            if (descriptor == null)
            {
                this.mColoredCircleBackground.getPaint().setColor(clusterColor);
                descriptor = BitmapDescriptorFactory.fromBitmap(this.mClusterIconGenerator.makeIcon(this.getClusterText(bucket)));
                this.mIcons.put(bucket, descriptor);
            }

            markerOptions.icon(descriptor);
        }

        private int getbackground(int[] ints)
        {
            int tmp = 0;
            for (int i = 1; i < 4; i++)
            {
                if (ints[i - 1] > ints[i]) tmp = i - 1;
            }
            switch (tmp)
            {
                case 0:
                    tmp = R.color.greenMarker;
                    break;
                case 1:
                    tmp = R.color.yellowMarker;
                    break;
                case 2:
                    tmp = R.color.orangeMarker;
                    break;
                case 3:
                    tmp = R.color.redMarker;
                    break;
                default:
                    tmp = R.color.greenMarker;
                    break;
            }
            return tmp;
        }

        private LayerDrawable makeClusterBackground()
        {
            // Outline color

            this.mColoredCircleBackground = new ShapeDrawable(new OvalShape());
            ShapeDrawable outline = new ShapeDrawable(new OvalShape());
//            outline.getPaint().setColor(clusterOutlineColor);
            LayerDrawable background = new LayerDrawable(new Drawable[]{outline, this.mColoredCircleBackground});
            int strokeWidth = (int) (this.mDensity * 3.0F);
            background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
            return background;
        }

        private SquareTextView makeSquareTextView()
        {
            SquareTextView squareTextView = new SquareTextView(getApplicationContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
            squareTextView.setLayoutParams(layoutParams);
            squareTextView.setId(com.google.maps.android.R.id.text);
            int twelveDpi = (int) (12.0F * this.mDensity);
            squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
            return squareTextView;
        }

        @Override
        protected void onBeforeClusterItemRendered(Event event, MarkerOptions markerOptions)
        {
            // Draw a single person.
            // Set the info window to show their name.

            BitmapDescriptor bitmapDescriptor;
            switch (event.getImportance())
            {
                case 0:
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    break;
                case 1:
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                    break;
                case 2:
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                    break;
                case 3:
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                default:
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                    break;
            }
//        marker.setIcon(bitmapDescriptor);
            markerOptions.icon(bitmapDescriptor);

        }


        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster)
        {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    private class CustomMarkerManager extends MarkerManager
    {

        public CustomMarkerManager(GoogleMap map)
        {
            super(map);
        }

        @Override
        public boolean onMarkerClick(Marker marker)
        {
            mMarkerToAdd = marker;
            if (marker.equals(mCurrentMarker))
            {
                Intent intent = new Intent(mContext, PictoSelectionActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, message);

                startActivityForResult(intent, 200);
                return false;
            }
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("debug", "onActivityResult:" + data.getIntExtra("id", -1));
        Log.d("debug", "onActivityResult:" + data.getIntExtra("importance", -1));
        /*Marker marker = mMap.addMarker(mMarkerToAdd);*/
        mMarkerCollection.addMarker(new MarkerOptions().position(mMarkerToAdd.getPosition()));
        mCurrentMarker.remove();
    }

    private void requestPerm()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else
            {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 3:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else
                {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
