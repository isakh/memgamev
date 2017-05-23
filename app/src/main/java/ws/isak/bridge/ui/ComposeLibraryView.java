package ws.isak.bridge.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;
import ws.isak.bridge.utils.ImageScaling;

/*
 *
 *
 * @author isak
 */

public class ComposeLibraryView extends LinearLayout implements View.OnClickListener { //FIXME do we need OnClickListener?

    public static final String TAG = "ComposeLibraryView";
    public static String URI_DRAWABLE = "drawable://";

    private int mScreenWidth;           //TODO make this a function (20%) of the screen width for the board
    private int mScreenHeight;

    //TODO array list for sample images?


    public ComposeLibraryView(Context context) {
        this(context, null);
        Log.d (TAG, "constructor");
    }

    public ComposeLibraryView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d (TAG, "overloaded constructor");
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        int margin = Shared.context.getResources().getDimensionPixelSize(R.dimen.compose_game_controls_margin_top);
        int padding = Shared.context.getResources().getDimensionPixelSize(R.dimen.compose_board_padding);

        mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding * 2;
        mScreenWidth = (int) Math.floor((getResources().getDisplayMetrics().widthPixels - padding*2 - ImageScaling.px(20)) * 0.15);    //TODO * proportion (currently 20%) of screen for view - make less of a hack
        Log.d (TAG, " ... mScreenHeight: " + mScreenHeight + " | mScreenWidth: " + mScreenWidth);
        setClipToPadding(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d (TAG, "method onFinishInflate");
    }

    public static ComposeLibraryView fromXml(Context context, ViewGroup parent) {
        Log.d (TAG, "method fromXml: inflating swap_controls_view: " +
                LayoutInflater.from(context).inflate(R.layout.compose_library_view, parent, false));
        return (ComposeLibraryView) LayoutInflater.from(context).inflate(R.layout.compose_library_view, parent, false);
    }

    @Override
    public void onClick (View v) {}

    //method populateSampleLibrary will fill the ComposeScrollingImageView LinearLayout with sample ImageView
    //this method will be called from ComposeGameFragment //build the library frame
    public void populateSampleLibrary () {
        Log.i(TAG, "method populateSampleLibrary: num samples to load: " + Shared.composeSampleDataList.size());
        //load the linear layout defined in xml
        LinearLayout libraryLayout = (LinearLayout) findViewById(R.id.compose_game_scrolling_images_linear_layout);
        libraryLayout.setGravity(Gravity.CENTER);
        libraryLayout.setBackgroundColor(0xAA00FF00);       //FIXME set in xml


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);

        //iterate over the samples loaded in the library
        for (int sampleNum = 0; sampleNum < Shared.composeSampleDataList.size(); sampleNum++) {
            addSample(sampleNum, libraryLayout, params);
        }
        libraryLayout.setClipChildren(false);
    }


    private void addSample (final int sampleNum, ViewGroup parent, LinearLayout.LayoutParams params) {

        Log.i (TAG, "method addSample: int sampleNum: " + sampleNum +
                " | init: parent.getVisibility: " + parent.getVisibility() +
                " | parent.isShown: " + parent.isShown());
        final ImageButton libraryEntry = new ImageButton (Shared.context);
        libraryEntry.setLayoutParams(params);
        parent.addView(libraryEntry);
        parent.setClipChildren(false);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                //create an image view for each sample
                int imgWidth;

                imgWidth = (int) (mScreenWidth - (Shared.context.getResources().getDimension(R.dimen.compose_game_library_image_padding)));
                Log.i (TAG, " method addSample: AsyncTask doInBackground: target imgWidth: " + imgWidth);
                String libEntryResourceName = Shared.composeSampleDataList.get(sampleNum).getSpectroURI().substring(URI_DRAWABLE.length());
                int libEntryResourceID = Shared.context.getResources().getIdentifier(libEntryResourceName, "drawable", Shared.context.getPackageName());
                Bitmap libEntryBitmap = ImageScaling.scaleDown(libEntryResourceID, imgWidth, imgWidth);   //third parameter ensure square output - shouldn't be an issue as source files are square
                Bitmap scaledLibEntryBitmap = ImageScaling.crop(libEntryBitmap, imgWidth, imgWidth);
                //FIXME REMOVE DEBUGGING CODE
                int w = scaledLibEntryBitmap.getWidth();
                int h = scaledLibEntryBitmap.getHeight();
                Log.i (TAG, "method addSample: AsyncTask doInBackground:: libEntryResourceName: " + libEntryResourceName +
                        " | bitmap width: " + w + " | bitmap height: " + h);
                return scaledLibEntryBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                //
                BitmapDrawable libEntryBitmapDrawable = new BitmapDrawable(Shared.context.getResources(), result);
                libraryEntry.setBackground(libEntryBitmapDrawable);
            }
        }.execute();

        //TODO add: libraryEntry.setOnClickListener();
        //TODO add border and buffer between buttons

    }

}