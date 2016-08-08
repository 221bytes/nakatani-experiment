package nakatani.ritsumeikan.alexandreexperiment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PictoSelectionActivity extends AppCompatActivity
{

    MyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picto_selection);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        final R.drawable drawableResources = new R.drawable();
        final Class<R.drawable> c = R.drawable.class;
        final Field[] fields = c.getDeclaredFields();
        final ArrayList<Field> pictos = new ArrayList<>();
        for (int i = 0, max = fields.length; i < max; i++)
        {
            try
            {
                Field f = fields[i];
                if (f.getName().contains("picto_"))
                {
                    pictos.add(f);
                }} catch (Exception e)
            {
                continue;
            }
    /* make use of resourceId for accessing Drawables here */
        }
        mAdapter = new MyAdapter(pictos, getApplicationContext());
        /*mAdapter.addListener(this);*/
/*
        mAdapter.addListener(mMainActivity);
*/
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mContext = this;
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        try
                        {
                            mId =  pictos.get(position).getInt(drawableResources);
                            Intent intent = new Intent(mContext, ColorSelectionActivity.class);
                            //intent.putExtra(EXTRA_MESSAGE, message);

                            startActivityForResult(intent, 200);


                        } catch (IllegalAccessException e)
                        {
                            e.printStackTrace();
                        }
// TODO Add extras or a data URI to this intent as appropriate.

                        // do whatever
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent resultIntent = new Intent();

        resultIntent.putExtra("id", mId);
        resultIntent.putExtra("importance", data.getIntExtra("importance", -1));

        setResult(Activity.RESULT_OK, resultIntent);

        finish();

    }}
