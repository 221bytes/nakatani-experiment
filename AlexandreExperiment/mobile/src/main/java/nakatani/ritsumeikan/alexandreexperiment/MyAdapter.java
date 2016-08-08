package nakatani.ritsumeikan.alexandreexperiment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/*
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
*/

import java.lang.reflect.Field;
import java.util.ArrayList;

import nakatani.ritsumeikan.alexandreexperiment.models.Pictogram;


/**
 * Created by Alex on 15/12/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{
    private Context mContext;
    private ArrayList<ClickOnPictogram> mClickOnPictograms = new ArrayList<>();


    public interface ClickOnPictogram
    {
        void onPictogramsClick(View view);
    }

    public void addListener(ClickOnPictogram listener)
    {
        mClickOnPictograms.add(listener);
    }

    private void fireOnPictogramsUpdate(View view)
    {
        for (ClickOnPictogram clickOnPictogram : mClickOnPictograms)
        {
            clickOnPictogram.onPictogramsClick(view);
        }
    }

    private ArrayList<Field> mDataset;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        private View mView;
        private ImageView mImageView;

        public ViewHolder(View v)
        {
            super(v);
            mView = v;
            mImageView = (ImageView) v.findViewById(R.id.PictogramSelectedImageButton);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
/*    public MyAdapter(ArrayList<Pictogram> myDataset, Context context)
    {
        mDataset = myDataset;
        mContext = context;
    }*/

    public MyAdapter(ArrayList<Field> myDataset, Context context)
    {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_image_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                fireOnPictogramsUpdate(v);
            }
        });
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
       /* String path = mDataset.get(position).getPath();*/
        ImageView imageView = holder.mImageView;
/*
        mContext.getResources().getString(R.string.server_url);
        String url = mContext.getResources().getString(R.string.server_url) + path;
        imageView.setImageUrl(url, mImageLoader);
*/
        final R.drawable drawableResources = new R.drawable();

        final int resourceId;
        try {
            resourceId = mDataset.get(position).getInt(drawableResources);
            imageView.setImageResource(resourceId);

        } catch (Exception e) {

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

}



