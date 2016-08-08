package nakatani.ritsumeikan.alexandreexperiment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ColorSelectionActivity extends AppCompatActivity
{

    private int mImportance;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selection);
        mContext = this;
    }

    public void onColorClick(View view)
    {

        switch (view.getId())
        {
            case R.id.imageButtonGreen:
                mImportance = 0;
                break;
            case R.id.imageButtonYellow:
                mImportance = 1;
                break;
            case R.id.imageButtonOrange:
                mImportance = 2;
                break;
            case R.id.imageButtonRed:
                mImportance = 3;
                break;
            default:
                mImportance = 0;
        }
        Intent resultIntent = new Intent();

        resultIntent.putExtra("importance", mImportance);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        // do whatever

    }
}
