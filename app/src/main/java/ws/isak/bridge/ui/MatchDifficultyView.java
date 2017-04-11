package ws.isak.bridge.ui;

import java.util.Locale;
import android.util.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ws.isak.bridge.R;
import ws.isak.bridge.common.Shared;

/*
 * Class MatchDifficultyView creates the view for the match difficulty screen
 *
 * @author isak
 */

public class MatchDifficultyView extends LinearLayout {

    public static final String TAG = "DifficultyView";
	private ImageView mTitle;

    /*
     * Constructor DifficultyView sets context
     */
	public MatchDifficultyView(Context context) {
		this(context, null);
        Log.d (TAG, "constructor");
	}

    /*
     * Overloaded constructor sets context and attributes
     */
	public MatchDifficultyView(Context context, AttributeSet attrs) {
		super(context, attrs);
        Log.d (TAG, "overloaded constructor, includes AttributeSet");
		LayoutInflater.from(context).inflate(R.layout.match_difficulty_view, this, true);
		setOrientation(LinearLayout.VERTICAL);
		mTitle = (ImageView) findViewById(R.id.match_difficulty_title);
	}

    /*
     * Method setMatchDifficulty
     */
	public void setMatchDifficulty(int difficulty, int stars) {
        Log.d (TAG, "method setMatchDifficulty");
		String titleResource = String.format(Locale.ENGLISH, "button_difficulty_%d_star_%d", difficulty, stars);
		int drawableResourceId = Shared.context.getResources().getIdentifier(titleResource, "drawable", Shared.context.getPackageName());
		mTitle.setImageResource(drawableResourceId);
	}
	
}
