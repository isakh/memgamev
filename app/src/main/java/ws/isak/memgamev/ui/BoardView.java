package ws.isak.memgamev.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.os.SystemClock;
import android.os.AsyncTask;

import android.util.Log;
import android.util.AttributeSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.LinearLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import ws.isak.memgamev.R;
import ws.isak.memgamev.common.Shared;
import ws.isak.memgamev.common.Music;
import ws.isak.memgamev.events.ui.FlipCardEvent;
import ws.isak.memgamev.events.engine.PlayCardAudioEvent;
import ws.isak.memgamev.model.BoardArrangement;
import ws.isak.memgamev.model.BoardConfiguration;
import ws.isak.memgamev.model.Game;
import ws.isak.memgamev.utils.Utils;

/*
 * Class BoardView comprises the code which builds the board according to the dimensions found in the
 * xml dimens file according to the the ratios of tiles to rows/columns that is defined in the
 * BoardConfiguration class given users' difficulty selection.  The board is a 2D array of tiles
 * each tile mapped to a card object.
  *
  * @author isak
 */

public class BoardView extends LinearLayout {

	public final String TAG = "Class: BoardView";

	private LinearLayout.LayoutParams mRowLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	private LinearLayout.LayoutParams mTileLayoutParams;
	private int mScreenWidth;
	private int mScreenHeight;
	private BoardConfiguration mBoardConfiguration;				//an instance of the board configuration for the current game
	private BoardArrangement mBoardArrangement;					//an instance of the board arrangement for the current game
	private Map<Integer, TileView> mViewReference;				//a mapping of each tile ID (integer curTileOnBoard) to a view TileView
	private List<Integer> flippedUp = new ArrayList<Integer>();		//an array list to hold the id's of the currently flipped up cards
	private boolean mLocked = false;								//a flag to keep track of whether one or two cards has been flipped
	private int mSize;												//the dimension of the tile to be drawn

	public BoardView(Context context) {
		this(context, null);
        Log.d (TAG, "constructor BoardView");
	}

	public BoardView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);
		int margin = getResources().getDimensionPixelSize(R.dimen.margin_top);
		int padding = getResources().getDimensionPixelSize(R.dimen.board_padding);
		mScreenHeight = getResources().getDisplayMetrics().heightPixels - margin - padding*2;
		mScreenWidth = getResources().getDisplayMetrics().widthPixels - padding*2 - Utils.px(20);
		mViewReference = new HashMap<Integer, TileView>();
		setClipToPadding(false);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public static BoardView fromXml(Context context, ViewGroup parent) {
		return (BoardView) LayoutInflater.from(context).inflate(R.layout.board_view, parent, false);
	}

	public void setBoard(Game game) {
		Log.d (TAG, "method setBoard ... at start");
		mBoardConfiguration = game.boardConfiguration;
		mBoardArrangement = game.boardArrangement;
		// calc prefered tiles in width and height
		int singleMargin = getResources().getDimensionPixelSize(R.dimen.card_margin);
		float density = getResources().getDisplayMetrics().density;
		singleMargin = Math.max((int) (1 * density), (int) (singleMargin - mBoardConfiguration.difficulty * 2 * density));
		int sumMargin = 0;
		for (int row = 0; row < mBoardConfiguration.numRows; row++) {
			sumMargin += singleMargin * 2;
		}
		int tilesHeight = (mScreenHeight - sumMargin) / mBoardConfiguration.numRows;
		int tilesWidth = (mScreenWidth - sumMargin) / mBoardConfiguration.numTilesInRow;
		mSize = Math.min(tilesHeight, tilesWidth);

		mTileLayoutParams = new LinearLayout.LayoutParams(mSize, mSize);
		mTileLayoutParams.setMargins(singleMargin, singleMargin, singleMargin, singleMargin);

		// build the ui
		Log.d (TAG, "method setBoard ... calling method buildBoard");
		buildBoard();
	}

	/*
	 * Build the board
	 */
	private void buildBoard() {
		//Log.d (TAG, "method: buildBoard");
		for (int row = 0; row < mBoardConfiguration.numRows; row++) {
			// add row
			addBoardRow(row);
		}

		setClipChildren(false);
	}

	/*
	 * Add each row to the board.
	 */
	private void addBoardRow(int rowNum) {
		//Log.d (TAG, "method: addBoardRow");
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER);

		for (int curTileInRow = 0; curTileInRow < mBoardConfiguration.numTilesInRow; curTileInRow++) {
			addTile(rowNum * mBoardConfiguration.numTilesInRow + curTileInRow, linearLayout);
		}

		// add to this view
		addView(linearLayout, mRowLayoutParams);
		linearLayout.setClipChildren(false);
	}

	/*
	 * Add each tile to the row.
	 */
	private void addTile(final int curTileOnBoard, ViewGroup parent) {
		//Log.d (TAG, "method addTile init");
		final TileView tileView = TileView.fromXml(getContext(), parent);
		tileView.setLayoutParams(mTileLayoutParams);
		parent.addView(tileView);
		parent.setClipChildren(false);
		mViewReference.put(curTileOnBoard, tileView);

		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				//Log.d (TAG, "*** method: addTile: new AsyncTask: override doInBackground: calling getTileBitmap: curTileOnBoard is: " + curTileOnBoard + " mSize is: " + mSize);
				return mBoardArrangement.getTileBitmap(curTileOnBoard, mSize);  //this gets one of two bitmaps depending on flags
			}
			
			@Override
			protected void onPostExecute(Bitmap result) {
				tileView.setTileImage(result);
			}
		}.execute();
		
		tileView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mLocked && tileView.isFlippedDown() && !Music.getIsAudioPlaying()) {
					Log.d (TAG, "			   : curTileOnBoard is: " + curTileOnBoard);
					Log.d (TAG, "			   : curCardOnTile is: " + mBoardArrangement.cardObjs.get(curTileOnBoard).getCardID());
					Log.d (TAG, " 			   : curCardOnTile.getAudioURI is: " + mBoardArrangement.cardObjs.get(curTileOnBoard).getAudioURI());
					Log.d (TAG, " 			   : curCardOnTile.getImageURI1 is: " + mBoardArrangement.cardObjs.get(curTileOnBoard).getImageURI1());
					Log.d (TAG, " 			   : curCardOnTile.getPairedImageDiffer is: " + mBoardArrangement.cardObjs.get(curTileOnBoard).getPairedImageDiffer());
					Log.d (TAG, " 			   : curCardOnTile.getFirstImageUsed is: " + mBoardArrangement.cardObjs.get(curTileOnBoard).getFirstImageUsed());
					Log.d (TAG, " 			   : curCardOnTile.getImageURI2 is: " + mBoardArrangement.cardObjs.get(curTileOnBoard).getImageURI2());

					//TODO If this is the first tile being clicked, we need to change the state of MemGameData.isGameStarted()
			  		//TODO 		- if this is the first tile we need to set the MemGameData.setGameStartTimeStamp()
					//TODO If this is not the first tile, we need to record that click has been made, it's time, and update accordingly
					//TODO should this be coded here, or sent as a new event (i.e. SaveTurnDataEvent)

					Log.d (TAG, "***** Update MemGameData with current timing information *****");
					Log.d (TAG, "Current SystemClock.elapsedReadTime(): " + SystemClock.elapsedRealtime());

					tileView.flipUp();
					//Log.d (TAG, " *** method addTile: onClick: called tileView.flipUp() *** ");
					flippedUp.add(curTileOnBoard);
					//Log.d (TAG, " *** flippedUp.size() is: " + flippedUp.size() + " *** ");
					if (flippedUp.size() == 2) {
						mLocked = true;
					}
					Log.d (TAG, "method addTile: tileView.setOnClickListener: Overriding onClick: new FlipCardEvent");
					Shared.eventBus.notify(new FlipCardEvent(curTileOnBoard));
					Log.d (TAG, "method addTile: tileView.setOnClickListener: Overriding onClick: new PlayCardAudioEvent");
					Shared.eventBus.notify(new PlayCardAudioEvent(curTileOnBoard));
				}
			}
		});

		ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(tileView, "scaleX", 0.8f, 1f);
		scaleXAnimator.setInterpolator(new BounceInterpolator());
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(tileView, "scaleY", 0.8f, 1f);
		scaleYAnimator.setInterpolator(new BounceInterpolator());
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
		animatorSet.setDuration(500);
		tileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animatorSet.start();
	}

	public void flipDownAll() {
		Log.d (TAG, "method flipDownAll ... at start");
		for (Integer id : flippedUp) {
		//for (int id = 0; id < flippedUp.size(); id++) {
			mViewReference.get(id).flipDown();
			Log.d (TAG, "method flipDownAll: current id in list flippedUp is: " + id);
		}
		flippedUp.clear();
		mLocked = false;
	}

	public void hideCards(int id1, int id2) {
		Log.d (TAG, "method hideCards ... at start");
		animateHide(mViewReference.get(id1));
		animateHide(mViewReference.get(id2));
		flippedUp.clear();
		mLocked = false;
	}

	protected void animateHide(final TileView v) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 0f);
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				v.setLayerType(View.LAYER_TYPE_NONE, null);
				v.setVisibility(View.INVISIBLE);
			}
		});
		animator.setDuration(100);
		v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		animator.start();
	}

}