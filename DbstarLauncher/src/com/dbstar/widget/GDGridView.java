package com.dbstar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Adapter;

import com.dbstar.R;

public class GDGridView extends GDAdapterView<Adapter> {

	private static final String TAG = "GDGridView";

	/**
	 * Disables stretching.
	 * 
	 * @see #setStretchMode(int)
	 */
	public static final int NO_STRETCH = 0;
	/**
	 * Stretches the spacing between columns.
	 * 
	 * @see #setStretchMode(int)
	 */
	public static final int STRETCH_SPACING = 1;
	/**
	 * Stretches columns.
	 * 
	 * @see #setStretchMode(int)
	 */
	public static final int STRETCH_COLUMN_WIDTH = 2;
	/**
	 * Stretches the spacing between columns. The spacing is uniform.
	 * 
	 * @see #setStretchMode(int)
	 */
	public static final int STRETCH_SPACING_UNIFORM = 3;

	/**
	 * Regular layout - usually an unsolicited layout from the view system
	 */
	static final int LAYOUT_NORMAL = 0;

	/**
	 * Show the first item
	 */
	static final int LAYOUT_FORCE_TOP = 1;

	/**
	 * Force the selected item to be on somewhere on the screen
	 */
	static final int LAYOUT_SET_SELECTION = 2;

	/**
	 * Show the last item
	 */
	static final int LAYOUT_FORCE_BOTTOM = 3;

	/**
	 * Make a mSelectedItem appear in a specific location and build the rest of
	 * the views from there. The top is specified by mSpecificTop.
	 */
	static final int LAYOUT_SPECIFIC = 4;

	/**
	 * Layout to sync as a result of a data change. Restore mSyncPosition to
	 * have its top at mSpecificTop
	 */
	static final int LAYOUT_SYNC = 5;

	/**
	 * Layout as a result of using the navigation keys
	 */
	static final int LAYOUT_MOVE_SELECTION = 6;

	int mLayoutMode = LAYOUT_NORMAL;

	/**
	 * Indicates whether the list is stacked from the bottom edge or the top
	 * edge.
	 */
	boolean mStackFromBottom;

	public static final int AUTO_FIT = -1;

	private int mNumColumns = AUTO_FIT;

	private int mHorizontalSpacing = 0;
	private int mRequestedHorizontalSpacing;
	private int mVerticalSpacing = 0;
	private int mColumnWidth;
	private int mColumnHeight;
	private int mRequestedColumnWidth;
	private int mRequestedColumnHeight;
	private int mRequestedNumColumns;
	private int mStretchMode = STRETCH_COLUMN_WIDTH;
	private int mGravity = Gravity.LEFT;

	/**
	 * Indicates that this list is always drawn on top of a solid, single-color,
	 * opaque background
	 */
	private int mCacheColorHint;

	/**
	 * If mAdapter != null, whenever this is true the adapter has stable IDs.
	 */
	boolean mAdapterHasStableIds;

	Adapter mAdapter;

	private DataSetObserver mDataSetObserver;

	private View mReferenceView = null;
	private View mReferenceViewInSelectedRow = null;

	final boolean[] mIsScrap = new boolean[1];
	/**
	 * Subclasses must retain their measure spec from onMeasure() into this
	 * member
	 */
	int mWidthMeasureSpec = 0;

	/*
	 * The selection's left padding
	 */
	int mSelectionLeftPadding = 0;

	/**
	 * The selection's top padding
	 */
	int mSelectionTopPadding = 0;

	/**
	 * The selection's right padding
	 */
	int mSelectionRightPadding = 0;

	/**
	 * The selection's bottom padding
	 */
	int mSelectionBottomPadding = 0;

	/**
	 * The current position of the selector in the list.
	 */
	int mSelectorPosition = INVALID_POSITION;

	/**
	 * Defines the selector's location and dimension at drawing time
	 */
	Rect mSelectorRect = new Rect();

	/**
     * The drawable used to draw the selector
     */
    Drawable mSelector;

	/**
	 * The offset in pixels form the top of the AdapterView to the top of the
	 * currently selected view. Used to save and restore state.
	 */
	int mSelectedTop = 0;
	
	/**
     * Indicates whether the list selector should be drawn on top of the children or behind
     */
    boolean mDrawSelectorOnTop = false;

	private final Rect mTempRect = new Rect();
	
	
	 /**
     * The top-level view of a list item can implement this interface to allow
     * itself to modify the bounds of the selection shown for that item.
     */
    public interface SelectionBoundsAdjuster {
        /**
         * Called to allow the list item to adjust the bounds shown for
         * its selection.
         *
         * @param bounds On call, this contains the bounds the list has
         * selected for the item (that is the bounds of the entire view).  The
         * values can be modified as desired.
         */
        public void adjustListItemSelectionBounds(Rect bounds);
    }
	

	public GDGridView(Context context) {
		super(context, null);
	}

	public GDGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGridView();

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.GDGridView);

		Drawable d = a.getDrawable(R.styleable.GDGridView_listSelector);
        if (d != null) {
            setSelector(d);
        }

        mDrawSelectorOnTop = a.getBoolean(
                R.styleable.GDGridView_drawSelectorOnTop, false);
        
		int color = a.getColor(R.styleable.GDGridView_cacheColorHint, 0);
		setCacheColorHint(color);

		int hSpacing = a.getDimensionPixelOffset(
				R.styleable.GDGridView_horizontalSpacing, 0);
		setHorizontalSpacing(hSpacing);

		int vSpacing = a.getDimensionPixelOffset(
				R.styleable.GDGridView_verticalSpacing, 0);
		setVerticalSpacing(vSpacing);

		int index = a.getInt(R.styleable.GDGridView_stretchMode,
				STRETCH_COLUMN_WIDTH);
		if (index >= 0) {
			setStretchMode(index);
		}

		int columnWidth = a.getDimensionPixelOffset(
				R.styleable.GDGridView_columnWidth, -1);
		if (columnWidth > 0) {
			setColumnWidth(columnWidth);
		}

		int columnHeight = a.getDimensionPixelOffset(
				R.styleable.GDGridView_columnHeight, -1);
		if (columnHeight > 0) {
			setColumnHeight(columnHeight);
		}

		int numColumns = a.getInt(R.styleable.GDGridView_numColumns, 1);
		setNumColumns(numColumns);

		index = a.getInt(R.styleable.GDGridView_android_gravity, -1);
		if (index >= 0) {
			setGravity(index);
		}

		a.recycle();
	}

	private void initGridView() {
		setFocusable(true);
		setWillNotDraw(false);
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	/**
	 * Sets the data behind this GridView.
	 * 
	 * @param adapter
	 *            the adapter providing the grid's data
	 */
	@Override
	public void setAdapter(Adapter adapter) {
		if (mAdapter != null && mDataSetObserver != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}

		resetList();
		mRecycler.clear();
		mAdapter = adapter;

		mOldSelectedPosition = INVALID_POSITION;
		mOldSelectedRowId = INVALID_ROW_ID;

		if (mAdapter != null) {
			mAdapterHasStableIds = mAdapter.hasStableIds();

			mOldItemCount = mItemCount;
			mItemCount = mAdapter.getCount();
			mDataChanged = true;
			checkFocus();

			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);

//			int position = 0;
//			position = lookForSelectablePosition(0, true);
//			setSelectedPositionInt(position);
//			setNextSelectedPositionInt(position);
//			checkSelectionChanged();

		} else {
			checkFocus();
			// Nothing selected
			checkSelectionChanged();
		}

		requestLayout();
	}

	@Override
	public View getSelectedView() {
		if (mItemCount > 0 && mSelectedPosition >= 0) {
			return getChildAt(mSelectedPosition - mFirstPosition);
		} else {
			return null;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

//		if (getCount() == 0) {
//			return;
//		}

		mInLayout = true;
		if (changed) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				getChildAt(i).forceLayout();
			}
			// mRecycler.markChildrenDirty();
		}

		layoutChildren();
		mInLayout = false;
	}

	protected void layoutChildren() {
//		Log.d(TAG, "layoutChildren");

		final boolean blockLayoutRequests = mBlockLayoutRequests;
		if (!blockLayoutRequests) {
			mBlockLayoutRequests = true;
		}

		try {
			 invalidate();

			if (mAdapter == null) {
				resetList();
				return;
			}

			final int childrenTop = getPaddingTop();
			// final int childrenBottom = getBottom() - getTop() -
			// getPaddingBottom();

			View sel = null;
			// Remember stuff we will need down below
			// Remember the previously selected view
//			int index = mSelectedPosition - mFirstPosition;

			boolean dataChanged = mDataChanged;
			if (dataChanged) {
				// handleDataChanged();
			}

			// Handle the empty set by removing all views that are visible
			// and calling it a day
			
//            Log.d(TAG, " mItemCount  " + mItemCount);

			if (mItemCount == 0) {
				resetList();
				return;
			}
			
			int oldSel = mSelectedPosition;
			if (mSelectedPosition != mNextSelectedPosition) {
				setSelectedPositionInt(mNextSelectedPosition);
			}

			// Pull all children into the RecycleBin.
			// These views will be reused if possible
			final int firstPosition = mFirstPosition;
			final RecycleBin recycleBin = mRecycler;

			if (oldSel != INVALID_POSITION) {
				// clean the old selected view in recycler
				recycleBin.put(oldSel, null);
			}
			
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				if (i != oldSel) {
					recycleBin.put(firstPosition + i, getChildAt(i));
				}
			}

			// Clear out old views
			detachAllViewsFromParent();

			mFirstPosition = 0;
			sel = fillFromTop(childrenTop);

			if (sel != null) {
				positionSelector(INVALID_POSITION, sel);
				mSelectedTop = sel.getTop();
			} else {
				mSelectedTop = 0;
				mSelectorRect.setEmpty();
			}
			
			mDataChanged = false;
			
			mNeedSync = false;
//            setNextSelectedPositionInt(mSelectedPosition);
            
			if (mItemCount > 0) {
//		    	Log.d(TAG, " mOldSelectedPosition " + mOldSelectedPosition + " mSelectedPosition " + mSelectedPosition);

                checkSelectionChanged();
            }

		} finally {
			if (!blockLayoutRequests) {
				mBlockLayoutRequests = false;
			}
		}
	}

	@Override
	protected void handleDataChanged() {
		int count = mItemCount;
		// int lastHandledItemCount = mLastHandledItemCount;
		// mLastHandledItemCount = mItemCount;

		// if (mChoiceMode != CHOICE_MODE_NONE && mAdapter != null &&
		// mAdapter.hasStableIds()) {
		// confirmCheckedPositionsById();
		// }

		// TODO: In the future we can recycle these views based on stable ID
		// instead.
		// mRecycler.clearTransientStateViews();

		if (count > 0) {
			int newPos;
			int selectablePos;

			// Find the row we are supposed to sync to
			if (mNeedSync) {
				// Update this first, since setNextSelectedPositionInt inspects
				// it
				mNeedSync = false;

				// if (mTranscriptMode == TRANSCRIPT_MODE_ALWAYS_SCROLL) {
				// mLayoutMode = LAYOUT_FORCE_BOTTOM;
				// return;
				// } else if (mTranscriptMode == TRANSCRIPT_MODE_NORMAL) {
				// if (mForceTranscriptScroll) {
				// mForceTranscriptScroll = false;
				// mLayoutMode = LAYOUT_FORCE_BOTTOM;
				// return;
				// }
				// final int childCount = getChildCount();
				// final int listBottom = getHeight() - getPaddingBottom();
				// final View lastChild = getChildAt(childCount - 1);
				// final int lastBottom = lastChild != null ?
				// lastChild.getBottom() : listBottom;
				// if (mFirstPosition + childCount >= lastHandledItemCount &&
				// lastBottom <= listBottom) {
				// mLayoutMode = LAYOUT_FORCE_BOTTOM;
				// return;
				// }
				// // Something new came in and we didn't scroll; give the user
				// a clue that
				// // there's something new.
				// awakenScrollBars();
				// }

				switch (mSyncMode) {
				case SYNC_SELECTED_POSITION:
					if (isInTouchMode()) {
						// We saved our state when not in touch mode. (We know
						// this because
						// mSyncMode is SYNC_SELECTED_POSITION.) Now we are
						// trying to
						// restore in touch mode. Just leave mSyncPosition as it
						// is (possibly
						// adjusting if the available range changed) and return.
						mLayoutMode = LAYOUT_SYNC;
						mSyncPosition = Math.min(Math.max(0, mSyncPosition),
								count - 1);

						return;
					} else {
						// See if we can find a position in the new data with
						// the same
						// id as the old selection. This will change
						// mSyncPosition.
						newPos = findSyncPosition();
						if (newPos >= 0) {
							// Found it. Now verify that new selection is still
							// selectable
							selectablePos = lookForSelectablePosition(newPos,
									true);
							if (selectablePos == newPos) {
								// Same row id is selected
								mSyncPosition = newPos;

								if (mSyncHeight == getHeight()) {
									// If we are at the same height as when we
									// saved state, try
									// to restore the scroll position too.
									mLayoutMode = LAYOUT_SYNC;
								} else {
									// We are not the same height as when the
									// selection was saved, so
									// don't try to restore the exact position
									mLayoutMode = LAYOUT_SET_SELECTION;
								}

								// Restore selection
								setNextSelectedPositionInt(newPos);
								return;
							}
						}
					}
					break;
				case SYNC_FIRST_POSITION:
					// Leave mSyncPosition as it is -- just pin to available
					// range
					mLayoutMode = LAYOUT_SYNC;
					mSyncPosition = Math.min(Math.max(0, mSyncPosition),
							count - 1);

					return;
				}
			}

			if (!isInTouchMode()) {
				// We couldn't find matching data -- try to use the same
				// position
				newPos = getSelectedItemPosition();

				// Pin position to the available range
				if (newPos >= count) {
					newPos = count - 1;
				}
				if (newPos < 0) {
					newPos = 0;
				}

				// Make sure we select something selectable -- first look down
				selectablePos = lookForSelectablePosition(newPos, true);

				if (selectablePos >= 0) {
					setNextSelectedPositionInt(selectablePos);
					return;
				} else {
					// Looking down didn't work -- try looking up
					selectablePos = lookForSelectablePosition(newPos, false);
					if (selectablePos >= 0) {
						setNextSelectedPositionInt(selectablePos);
						return;
					}
				}
			} else {

				// We already know where we want to resurrect the selection
				// if (mResurrectToPosition >= 0) {
				// return;
				// }
			}

		}

		// Nothing is selected. Give up and reset everything.
		mLayoutMode = mStackFromBottom ? LAYOUT_FORCE_BOTTOM : LAYOUT_FORCE_TOP;
		mSelectedPosition = INVALID_POSITION;
		mSelectedRowId = INVALID_ROW_ID;
		mNextSelectedPosition = INVALID_POSITION;
		mNextSelectedRowId = INVALID_ROW_ID;
		mNeedSync = false;
		mSelectorPosition = INVALID_POSITION;
		checkSelectionChanged();
	}

	@Override
	int lookForSelectablePosition(int position, boolean lookDown) {
		final Adapter adapter = mAdapter;
		if (adapter == null || isInTouchMode()) {
			return INVALID_POSITION;
		}

		if (position < 0 || position >= mItemCount) {
			return INVALID_POSITION;
		}
		return position;
	}

	/**
	 * Fills the list from pos down to the end of the list view.
	 * 
	 * @param pos
	 *            The first position to put in the list
	 * 
	 * @param nextTop
	 *            The location where the top of the item associated with pos
	 *            should be drawn
	 * 
	 * @return The view that is currently selected, if it happens to be in the
	 *         range that we draw.
	 */
	private View fillDown(int pos, int nextTop) {
		View selectedView = null;

		int end = (getBottom() - getTop());
		end -= getPaddingBottom();

		while (nextTop < end && pos < mItemCount) {
			View temp = makeRow(pos, nextTop, true);
			if (temp != null) {
				selectedView = temp;
			}

			int lastBottom = 0;
			if (mColumnHeight > 0) {
				int gravity = mGravity;
				switch (gravity) {
				case Gravity.CENTER:
				case Gravity.CENTER_VERTICAL:
					lastBottom = mReferenceView.getBottom()
							+ (mColumnHeight - mReferenceView
									.getMeasuredHeight()) / 2;
				}
			} else {
				lastBottom = mReferenceView.getBottom();
			}
			// mReferenceView will change with each call to makeRow()
			// do not cache in a local variable outside of this loop
			nextTop = lastBottom + mVerticalSpacing;

//			//Log.d(TAG, "next top = " + nextTop);

			pos += mNumColumns;
		}

		// setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
		// - 1);
		return selectedView;
	}

	/**
	 * Fills the list from pos up to the top of the list view.
	 * 
	 * @param pos
	 *            The first position to put in the list
	 * 
	 * @param nextBottom
	 *            The location where the bottom of the item associated with pos
	 *            should be drawn
	 * 
	 * @return The view that is currently selected
	 */
	private View fillUp(int pos, int nextBottom) {
		View selectedView = null;

		int end = 0;
		// if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
		// end = mListPadding.top;
		// }

		end = getPaddingTop();

		while (nextBottom > end && pos >= 0) {

			View temp = makeRow(pos, nextBottom, false);
			if (temp != null) {
				selectedView = temp;
			}

			nextBottom = mReferenceView.getTop() - mVerticalSpacing;

			mFirstPosition = pos;

			pos -= mNumColumns;
		}

		if (mStackFromBottom) {
			mFirstPosition = Math.max(0, pos + 1);
		}

		// setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
		// - 1);
		return selectedView;
	}

	/**
	 * Fills the list from top to bottom, starting with mFirstPosition
	 * 
	 * @param nextTop
	 *            The location where the top of the first item should be drawn
	 * 
	 * @return The view that is currently selected
	 */
	private View fillFromTop(int nextTop) {
		// mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
		// mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		// if (mFirstPosition < 0) {
		// mFirstPosition = 0;
		// }
		// mFirstPosition -= mFirstPosition % mNumColumns;
		return fillDown(mFirstPosition, nextTop);
	}

	private View makeRow(int startPos, int y, boolean flow) {
		final int columnWidth = mColumnWidth;
		final int horizontalSpacing = mHorizontalSpacing;

		int last;
		int nextLeft = getPaddingLeft();// + horizontalSpacing;

		last = Math.min(startPos + mNumColumns, mItemCount);

		View selectedView = null;

		final int selectedPosition = mSelectedPosition;

		View child = null;
		for (int pos = startPos; pos < last; pos++) {
			// is this the selected item?
			boolean selected = pos == selectedPosition;
			// does the list view have focus or contain focus

			final int where = flow ? -1 : pos - startPos;
			child = makeAndAddView(pos, y, flow, nextLeft, selected, where);

			nextLeft += columnWidth;
			if (pos < last - 1) {
				nextLeft += horizontalSpacing;
			}

			if (selected) {
				selectedView = child;
			}
		}

		mReferenceView = child;

		if (selectedView != null) {
			mReferenceViewInSelectedRow = mReferenceView;
		}

		return selectedView;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Sets up mListPadding
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode == MeasureSpec.UNSPECIFIED) {
			if (mColumnWidth > 0) {
				widthSize = mColumnWidth + getPaddingLeft() + getPaddingRight();
			} else {
				widthSize = getPaddingLeft() + getPaddingRight();
			}
//			widthSize += getVerticalScrollbarWidth();
		}

		int childWidth = widthSize - getPaddingLeft() - getPaddingRight();
		boolean didNotInitiallyFit = determineColumns(childWidth);
		mColumnHeight = mRequestedColumnHeight;

		int childHeight = 0;
		int childState = 0;

		mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
		final int count = mItemCount;
		if (count > 0) {
			final View child = obtainView(0, mIsScrap);

			GDGridView.LayoutParams p = (GDGridView.LayoutParams) child
					.getLayoutParams();
			if (p == null) {
				p = (GDGridView.LayoutParams) generateDefaultLayoutParams();
				child.setLayoutParams(p);
			}
			
//			p.viewType = mAdapter.getItemViewType(0);
//			p.forceAdd = true;

			int childHeightSpec = getChildMeasureSpec(
					MeasureSpec.makeMeasureSpec(mColumnHeight,
							MeasureSpec.AT_MOST), 0, p.height);
			int childWidthSpec = getChildMeasureSpec(
					MeasureSpec.makeMeasureSpec(mColumnWidth,
							MeasureSpec.AT_MOST), 0, p.width);
			child.measure(childWidthSpec, childHeightSpec);

			childHeight = child.getMeasuredHeight();
			childHeight = Math.max(childHeight, mColumnHeight);

			childState = combineMeasuredStates(childState,
					child.getMeasuredState());

			// if (mRecycler.shouldRecycleViewType(p.viewType)) {
			// mRecycler.addScrapView(child, -1);
			// }
		}

		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = getPaddingTop() + getPaddingBottom() + mColumnHeight
					+ getVerticalFadingEdgeLength() * 2;
		}

		if (heightMode == MeasureSpec.AT_MOST) {
			int ourSize = getPaddingTop() + getPaddingBottom();

			final int numColumns = mNumColumns;
			for (int i = 0; i < count; i += numColumns) {
				ourSize += childHeight;
				if (i + numColumns < count) {
					ourSize += mVerticalSpacing;
				}
				if (ourSize >= heightSize) {
					ourSize = heightSize;
					break;
				}
			}
			heightSize = ourSize;
		}

		if (widthMode == MeasureSpec.AT_MOST
				&& mRequestedNumColumns != AUTO_FIT) {
			int ourSize = (mRequestedNumColumns * mColumnWidth)
					+ ((mRequestedNumColumns - 1) * mHorizontalSpacing)
					+ getPaddingLeft() + getPaddingRight();
			if (ourSize > widthSize || didNotInitiallyFit) {
				widthSize |= MEASURED_STATE_TOO_SMALL;
			}
		}

		setMeasuredDimension(widthSize, heightSize);
		// mWidthMeasureSpec = widthMeasureSpec;
	}

	/**
	 * Get a view and have it show the data associated with the specified
	 * position. This is called when we have already discovered that the view is
	 * not available for reuse in the recycle bin. The only choices left are
	 * converting an old view or making a new one.
	 * 
	 * @param position
	 *            The position to display
	 * @param isScrap
	 *            Array of at least 1 boolean, the first entry will become true
	 *            if the returned view was taken from the scrap heap, false if
	 *            otherwise.
	 * 
	 * @return A view displaying the data associated with the specified position
	 */
	View obtainView(int position, boolean[] isScrap) {
		isScrap[0] = false;

		View child;
		child = mAdapter.getView(position, null, this);
		mRecycler.put(position, child);


		return child;
	}

	@Override
	protected void attachLayoutAnimationParameters(View child,
			ViewGroup.LayoutParams params, int index, int count) {

		GridLayoutAnimationController.AnimationParameters animationParams = (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

		if (animationParams == null) {
			animationParams = new GridLayoutAnimationController.AnimationParameters();
			params.layoutAnimationParameters = animationParams;
		}

		animationParams.count = count;
		animationParams.index = index;
		animationParams.columnsCount = mNumColumns;
		animationParams.rowsCount = count / mNumColumns;

		if (!mStackFromBottom) {
			animationParams.column = index % mNumColumns;
			animationParams.row = index / mNumColumns;
		} else {
			final int invertedIndex = count - 1 - index;

			animationParams.column = mNumColumns - 1
					- (invertedIndex % mNumColumns);
			animationParams.row = animationParams.rowsCount - 1 - invertedIndex
					/ mNumColumns;
		}
	}

	/**
	 * Obtain the view and add it to our list of children. The view can be made
	 * fresh, converted from an unused view, or used as is if it was in the
	 * recycle bin.
	 * 
	 * @param position
	 *            Logical position in the list
	 * @param y
	 *            Top or bottom edge of the view to add
	 * @param flow
	 *            if true, align top edge to y. If false, align bottom edge to
	 *            y.
	 * @param childrenLeft
	 *            Left edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @param where
	 *            to add new item in the list
	 * @return View that was added
	 */
	private View makeAndAddView(int position, int y, boolean flow,
			int childrenLeft, boolean selected, int where) {
		View child;
		
		if (selected) {
			child = mAdapter.getView(position, null, this);
//			mRecycler.put(position, child);
			setupChild(child, position, y, flow, childrenLeft, selected,
					false, where);
			
			return child;
		}

		if (!mDataChanged) {
			// Try to use an existing view for this position
			// child = mRecycler.getActiveView(position);
			child = mRecycler.get(position);
			if (child != null) {
				// Found it -- we're using an existing child
				// This just needs to be positioned
				setupChild(child, position, y, flow, childrenLeft, selected,
						true, where);
				return child;
			}
		}

		// Make a new view for this position, or convert an unused view if
		// possible
		child = obtainView(position, mIsScrap);

		// This needs to be positioned and measured
		setupChild(child, position, y, flow, childrenLeft, selected,
				mIsScrap[0], where);

		return child;
	}

	/**
	 * Add a view as a child and make sure it is measured (if necessary) and
	 * positioned properly.
	 * 
	 * @param child
	 *            The view to add
	 * @param position
	 *            The position of the view
	 * @param y
	 *            The y position relative to which this view will be positioned
	 * @param flow
	 *            if true, align top edge to y. If false, align bottom edge to
	 *            y.
	 * @param childrenLeft
	 *            Left edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @param recycled
	 *            Has this view been pulled from the recycle bin? If so it does
	 *            not need to be remeasured.
	 * @param where
	 *            Where to add the item in the list
	 * 
	 */
	private void setupChild(View child, int position, int y, boolean flow,
			int childrenLeft, boolean selected, boolean recycled, int where) {
		boolean isSelected = selected;// selected && shouldShowSelector();
		final boolean updateChildSelected = isSelected != child.isSelected();

		boolean needToMeasure = !recycled || updateChildSelected
				|| child.isLayoutRequested();

		// Respect layout params that are already in the view. Otherwise make
		// some up...
		GDGridView.LayoutParams p = (GDGridView.LayoutParams) child
				.getLayoutParams();
		if (p == null) {
			p = (GDGridView.LayoutParams) generateDefaultLayoutParams();
		}

		p.viewType = mAdapter.getItemViewType(position);

//		if (recycled && !p.forceAdd) {
		if (recycled) {
			attachViewToParent(child, where, p);
		} else {
//			p.forceAdd = false;
			addViewInLayout(child, where, p, true);
		}



//		//Log.d(TAG, " p.w=" + p.width + " p.h=" + p.height);

		if (needToMeasure) {
			int childHeightSpec = ViewGroup.getChildMeasureSpec(MeasureSpec
					.makeMeasureSpec(mColumnHeight, MeasureSpec.AT_MOST), 0,
					p.height);

			int childWidthSpec = ViewGroup.getChildMeasureSpec(MeasureSpec
					.makeMeasureSpec(mColumnWidth, MeasureSpec.AT_MOST), 0,
					p.width);
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();

		int childLeft;
		int childTop = flow ? y : y - h;

		final int gravity = mGravity;
		switch (gravity) {
		case Gravity.LEFT:
			childLeft = childrenLeft;
			break;
		case Gravity.CENTER_HORIZONTAL:
		case Gravity.CENTER:
			childLeft = childrenLeft + ((mColumnWidth - w) / 2);
			break;
		case Gravity.RIGHT:
			childLeft = childrenLeft + mColumnWidth - w;
			break;
		default:
			childLeft = childrenLeft;
			break;
		}

		switch (gravity) {
		case Gravity.TOP:
			childTop = flow ? y : y - h;
			break;
		case Gravity.CENTER_VERTICAL:
		case Gravity.CENTER:
			if (flow) {
				int top = y + (mColumnHeight - h) / 2;
				childTop = top;
			} else {
				int top = y + (mColumnHeight - h) / 2 - h;
				childTop = top;
			}
			break;
		}

		if (needToMeasure) {
			final int childRight = childLeft + w;
			final int childBottom = childTop + h;
//			//Log.d(TAG, "setupchild childLeft " + childLeft + " r " + childTop
//					+ " w " + w + " h " + h);
			child.layout(childLeft, childTop, childRight, childBottom);
		} else {
			child.offsetLeftAndRight(childLeft - child.getLeft());
			child.offsetTopAndBottom(childTop - child.getTop());
		}
	}
	
	public void clearSelection() {
		mNextSelectedPosition = INVALID_POSITION;
		mNextSelectedRowId = INVALID_ROW_ID;
		
		requestLayout();
	}

	/**
	 * Sets the currently selected item
	 * 
	 * @param position
	 *            Index (starting at 0) of the data item to be selected.
	 * 
	 *            If in touch mode, the item will not be selected but it will
	 *            still be positioned appropriately.
	 */
	@Override
	public void setSelection(int position) {
		// if (!isInTouchMode()) {
		// setNextSelectedPositionInt(position);
		// }

		setNextSelectedPositionInt(position);
		
		//selectionChanged();

		mLayoutMode = LAYOUT_SET_SELECTION;
		requestLayout();
	}

	/**
	 * Makes the item at the supplied position selected.
	 * 
	 * @param position
	 *            the position of the new selection
	 */
	void setSelectionInt(int position) {
//		int previousSelectedPosition = mNextSelectedPosition;
		setNextSelectedPositionInt(position);

		//Log.d(TAG, " old pos=" + previousSelectedPosition + " new pos = " + position);

//		if (position != previousSelectedPosition) {
//			selectionChanged();
//		}

		layoutChildren();
	}
	
	@Override
    public void requestLayout() {
        if (!mBlockLayoutRequests && !mInLayout) {
            super.requestLayout();
        }
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return commonKey(keyCode, 1, event);
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return commonKey(keyCode, repeatCount, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return commonKey(keyCode, 1, event);
	}

	private boolean commonKey(int keyCode, int count, KeyEvent event) {
		if (mAdapter == null) {
			return false;
		}

		// if (mDataChanged) {
		// layoutChildren();
		// }

//		Log.d(TAG, "commonKey keycode= " + keyCode);

		boolean handled = false;
		int action = event.getAction();

		if (action != KeyEvent.ACTION_UP) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (event.hasNoModifiers()) {
					handled = arrowScroll(FOCUS_LEFT);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (event.hasNoModifiers()) {
					handled = arrowScroll(FOCUS_RIGHT);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_UP:
				if (event.hasNoModifiers()) {
					handled = arrowScroll(FOCUS_UP);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (event.hasNoModifiers()) {
					handled = arrowScroll(FOCUS_DOWN);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
//				if (event.hasNoModifiers()) {
//					handled = true;// resurrectSelectionIfNeeded();
//					if (!handled && event.getRepeatCount() == 0
//							&& getChildCount() > 0) {
//						keyPressed();
//						handled = true;
//					}
//				}
				break;

			}
		}

		if (handled) {
			return true;
		}
		
		return false;
	}

	/**
	 * Sets the selector state to "pressed" and posts a CheckForKeyLongPress to
	 * see if this is a long press.
	 */
	void keyPressed() {
		if (!isEnabled() || !isClickable()) {
			return;
		}

		Drawable selector = mSelector;
		Rect selectorRect = mSelectorRect;
		// if (selector != null && (isFocused() ||
		// touchModeDrawsInPressedState())
		// && !selectorRect.isEmpty())
		if (selector != null && isFocused() && !selectorRect.isEmpty()) {

			final View v = getChildAt(mSelectedPosition - mFirstPosition);

			if (v != null) {
				if (v.hasFocusable())
					return;
				v.setPressed(true);
			}
			setPressed(true);

			final boolean longClickable = isLongClickable();
			Drawable d = selector.getCurrent();
			if (d != null && d instanceof TransitionDrawable) {
				if (longClickable) {
					((TransitionDrawable) d).startTransition(ViewConfiguration
							.getLongPressTimeout());
				} else {
					((TransitionDrawable) d).resetTransition();
				}
			}
		}
	}

	/**
	 * Scrolls up or down by the number of items currently present on screen.
	 * 
	 * @param direction
	 *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
	 * @return whether selection was moved
	 */
	boolean pageScroll(int direction) {
		int nextPage = -1;

		if (direction == FOCUS_UP) {
			nextPage = Math.max(0, mSelectedPosition - getChildCount());
		} else if (direction == FOCUS_DOWN) {
			nextPage = Math.min(mItemCount - 1, mSelectedPosition
					+ getChildCount());
		}

		if (nextPage >= 0) {
			setSelectionInt(nextPage);
			// invokeOnItemScrollListener();
			// awakenScrollBars();
			return true;
		}

		return false;
	}

	/**
	 * Go to the last or first item if possible.
	 * 
	 * @param direction
	 *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}.
	 * 
	 * @return Whether selection was moved.
	 */
	boolean fullScroll(int direction) {
		boolean moved = false;
		if (direction == FOCUS_UP) {
			mLayoutMode = LAYOUT_SET_SELECTION;
			setSelectionInt(0);
			// invokeOnItemScrollListener();
			moved = true;
		} else if (direction == FOCUS_DOWN) {
			mLayoutMode = LAYOUT_SET_SELECTION;
			setSelectionInt(mItemCount - 1);
			// invokeOnItemScrollListener();
			moved = true;
		}

		if (moved) {
			awakenScrollBars();
		}

		return moved;
	}

	/**
	 * Scrolls to the next or previous item, horizontally or vertically.
	 * 
	 * @param direction
	 *            either {@link View#FOCUS_LEFT}, {@link View#FOCUS_RIGHT},
	 *            {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
	 * 
	 * @return whether selection was moved
	 */
	boolean arrowScroll(int direction) {

		final int selectedPosition = mSelectedPosition;
		final int numColumns = mNumColumns;

		int startOfRowPos;
		int endOfRowPos;

		boolean moved = false;

		startOfRowPos = (selectedPosition / numColumns) * numColumns;
		endOfRowPos = Math.min(startOfRowPos + numColumns - 1, mItemCount - 1);

		switch (direction) {
		case FOCUS_UP:
			if (startOfRowPos > 0) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(Math.max(0, selectedPosition - numColumns));
				moved = true;
			}
			break;
		case FOCUS_DOWN:
			if (endOfRowPos < mItemCount - 1) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(Math.min(selectedPosition + numColumns,
						mItemCount - 1));
				moved = true;
			}
			break;
		case FOCUS_LEFT:
			if (selectedPosition > startOfRowPos) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(Math.max(0, selectedPosition - 1));
				moved = true;
			}
			break;
		case FOCUS_RIGHT:
			if (selectedPosition < endOfRowPos) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(Math.min(selectedPosition + 1, mItemCount - 1));
				moved = true;
			}
			break;
		}

		if (moved) {
			playSoundEffect(SoundEffectConstants
					.getContantForFocusDirection(direction));
		}

		return moved;
	}

	/**
	 * Goes to the next or previous item according to the order set by the
	 * adapter.
	 */
	boolean sequenceScroll(int direction) {
		int selectedPosition = mSelectedPosition;
		int numColumns = mNumColumns;
		int count = mItemCount;

		int startOfRow;
		int endOfRow;
		if (!mStackFromBottom) {
			startOfRow = (selectedPosition / numColumns) * numColumns;
			endOfRow = Math.min(startOfRow + numColumns - 1, count - 1);
		} else {
			int invertedSelection = count - 1 - selectedPosition;
			endOfRow = count - 1 - (invertedSelection / numColumns)
					* numColumns;
			startOfRow = Math.max(0, endOfRow - numColumns + 1);
		}

		boolean moved = false;
		boolean showScroll = false;
		switch (direction) {
		case FOCUS_FORWARD:
			if (selectedPosition < count - 1) {
				// Move to the next item.
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(selectedPosition + 1);
				moved = true;
				// Show the scrollbar only if changing rows.
				showScroll = selectedPosition == endOfRow;
			}
			break;

		case FOCUS_BACKWARD:
			if (selectedPosition > 0) {
				// Move to the previous item.
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(selectedPosition - 1);
				moved = true;
				// Show the scrollbar only if changing rows.
				showScroll = selectedPosition == startOfRow;
			}
			break;
		}

		if (moved) {
			playSoundEffect(SoundEffectConstants
					.getContantForFocusDirection(direction));
			// invokeOnItemScrollListener();
		}

		if (showScroll) {
			awakenScrollBars();
		}

		return moved;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		int closestChildIndex = -1;
		if (gainFocus && previouslyFocusedRect != null) {
			previouslyFocusedRect.offset(getScrollX(), getScrollY());

			// figure out which item should be selected based on previously
			// focused rect
			Rect otherRect = mTempRect;
			int minDistance = Integer.MAX_VALUE;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				// only consider view's on appropriate edge of grid
				if (!isCandidateSelection(i, direction)) {
					continue;
				}

				final View other = getChildAt(i);
				other.getDrawingRect(otherRect);
				offsetDescendantRectToMyCoords(other, otherRect);
				int distance = getDistance(previouslyFocusedRect, otherRect,
						direction);

				if (distance < minDistance) {
					minDistance = distance;
					closestChildIndex = i;
				}
			}
		}

		if (closestChildIndex >= 0) {
			setSelection(closestChildIndex + mFirstPosition);
		} else {
			requestLayout();
		}
	}

	/**
	 * What is the distance between the source and destination rectangles given
	 * the direction of focus navigation between them? The direction basically
	 * helps figure out more quickly what is self evident by the relationship
	 * between the rects...
	 * 
	 * @param source
	 *            the source rectangle
	 * @param dest
	 *            the destination rectangle
	 * @param direction
	 *            the direction
	 * @return the distance between the rectangles
	 */
	static int getDistance(Rect source, Rect dest, int direction) {
		int sX, sY; // source x, y
		int dX, dY; // dest x, y
		switch (direction) {
		case View.FOCUS_RIGHT:
			sX = source.right;
			sY = source.top + source.height() / 2;
			dX = dest.left;
			dY = dest.top + dest.height() / 2;
			break;
		case View.FOCUS_DOWN:
			sX = source.left + source.width() / 2;
			sY = source.bottom;
			dX = dest.left + dest.width() / 2;
			dY = dest.top;
			break;
		case View.FOCUS_LEFT:
			sX = source.left;
			sY = source.top + source.height() / 2;
			dX = dest.right;
			dY = dest.top + dest.height() / 2;
			break;
		case View.FOCUS_UP:
			sX = source.left + source.width() / 2;
			sY = source.top;
			dX = dest.left + dest.width() / 2;
			dY = dest.bottom;
			break;
		case View.FOCUS_FORWARD:
		case View.FOCUS_BACKWARD:
			sX = source.right + source.width() / 2;
			sY = source.top + source.height() / 2;
			dX = dest.left + dest.width() / 2;
			dY = dest.top + dest.height() / 2;
			break;
		default:
			throw new IllegalArgumentException("direction must be one of "
					+ "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, "
					+ "FOCUS_FORWARD, FOCUS_BACKWARD}.");
		}
		int deltaX = dX - sX;
		int deltaY = dY - sY;
		return deltaY * deltaY + deltaX * deltaX;
	}

	/**
	 * Is childIndex a candidate for next focus given the direction the focus
	 * change is coming from?
	 * 
	 * @param childIndex
	 *            The index to check.
	 * @param direction
	 *            The direction, one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT,
	 *            FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}
	 * @return Whether childIndex is a candidate.
	 */
	private boolean isCandidateSelection(int childIndex, int direction) {
		final int count = getChildCount();
		final int invertedIndex = count - 1 - childIndex;

		int rowStart;
		int rowEnd;

		if (!mStackFromBottom) {
			rowStart = childIndex - (childIndex % mNumColumns);
			rowEnd = Math.max(rowStart + mNumColumns - 1, count);
		} else {
			rowEnd = count - 1
					- (invertedIndex - (invertedIndex % mNumColumns));
			rowStart = Math.max(0, rowEnd - mNumColumns + 1);
		}

		switch (direction) {
		case View.FOCUS_RIGHT:
			// coming from left, selection is only valid if it is on left
			// edge
			return childIndex == rowStart;
		case View.FOCUS_DOWN:
			// coming from top; only valid if in top row
			return rowStart == 0;
		case View.FOCUS_LEFT:
			// coming from right, must be on right edge
			return childIndex == rowEnd;
		case View.FOCUS_UP:
			// coming from bottom, need to be in last row
			return rowEnd == count - 1;
		case View.FOCUS_FORWARD:
			// coming from top-left, need to be first in top row
			return childIndex == rowStart && rowStart == 0;
		case View.FOCUS_BACKWARD:
			// coming from bottom-right, need to be last in bottom row
			return childIndex == rowEnd && rowEnd == count - 1;
		default:
			throw new IllegalArgumentException("direction must be one of "
					+ "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, "
					+ "FOCUS_FORWARD, FOCUS_BACKWARD}.");
		}
	}

	void resetList() {
//		Log.d(TAG, " resetList ");

		removeAllViewsInLayout();
		mFirstPosition = 0;
		mDataChanged = false;
		mNeedSync = false;
		mOldSelectedPosition = INVALID_POSITION;
		mOldSelectedRowId = INVALID_ROW_ID;
		setSelectedPositionInt(INVALID_POSITION);
		setNextSelectedPositionInt(INVALID_POSITION);
		invalidate();
	}

	private boolean determineColumns(int availableSpace) {
		final int requestedHorizontalSpacing = mRequestedHorizontalSpacing;
		final int stretchMode = mStretchMode;
		final int requestedColumnWidth = mRequestedColumnWidth;
		boolean didNotInitiallyFit = false;

		if (mRequestedNumColumns == AUTO_FIT) {
			if (requestedColumnWidth > 0) {
				// Client told us to pick the number of columns
				mNumColumns = (availableSpace + requestedHorizontalSpacing)
						/ (requestedColumnWidth + requestedHorizontalSpacing);
			} else {
				// Just make up a number if we don't have enough info
				mNumColumns = 2;
			}
		} else {
			// We picked the columns
			mNumColumns = mRequestedNumColumns;
		}

		if (mNumColumns <= 0) {
			mNumColumns = 1;
		}

		switch (stretchMode) {
		case NO_STRETCH:
			// Nobody stretches
			mColumnWidth = requestedColumnWidth;
			mHorizontalSpacing = requestedHorizontalSpacing;
			break;

		default:
			int spaceLeftOver = availableSpace
					- (mNumColumns * requestedColumnWidth)
					- ((mNumColumns - 1) * requestedHorizontalSpacing);

			if (spaceLeftOver < 0) {
				didNotInitiallyFit = true;
			}

			switch (stretchMode) {
			case STRETCH_COLUMN_WIDTH:
				// Stretch the columns
				mColumnWidth = requestedColumnWidth + spaceLeftOver
						/ mNumColumns;
				mHorizontalSpacing = requestedHorizontalSpacing;
				break;

			case STRETCH_SPACING:
				// Stretch the spacing between columns
				mColumnWidth = requestedColumnWidth;
				if (mNumColumns > 1) {
					mHorizontalSpacing = requestedHorizontalSpacing
							+ spaceLeftOver / (mNumColumns - 1);
				} else {
					mHorizontalSpacing = requestedHorizontalSpacing
							+ spaceLeftOver;
				}
				break;

			case STRETCH_SPACING_UNIFORM:
				// Stretch the spacing between columns
				mColumnWidth = requestedColumnWidth;
				if (mNumColumns > 1) {
					mHorizontalSpacing = requestedHorizontalSpacing
							+ spaceLeftOver / (mNumColumns + 1);
				} else {
					mHorizontalSpacing = requestedHorizontalSpacing
							+ spaceLeftOver;
				}
				break;
			}

			break;
		}
		return didNotInitiallyFit;
	}	
	
	boolean mIsChildViewEnabled;
	void positionSelector(int position, View sel) {
        if (position != INVALID_POSITION) {
            mSelectorPosition = position;
        }

        final Rect selectorRect = mSelectorRect;
        selectorRect.set(sel.getLeft() - 4 , sel.getTop() - 4, sel.getRight() + 4, sel.getBottom() + 4);
        if (sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster)sel).adjustListItemSelectionBounds(selectorRect);
        }
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right,
                selectorRect.bottom);

        final boolean isChildViewEnabled = mIsChildViewEnabled;
        if (sel.isEnabled() != isChildViewEnabled) {
            mIsChildViewEnabled = !isChildViewEnabled;
            if (getSelectedItemPosition() != INVALID_POSITION) {
                refreshDrawableState();
            }
        }
    }

    private void positionSelector(int l, int t, int r, int b) {
        mSelectorRect.set(l - mSelectionLeftPadding, t - mSelectionTopPadding, r
                + mSelectionRightPadding, b + mSelectionBottomPadding);
    }
    
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
//        int saveCount = 0;
//        final boolean clipToPadding = (mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK;
//        if (clipToPadding) {
//            saveCount = canvas.save();
//            final int scrollX = mScrollX;
//            final int scrollY = mScrollY;
//            canvas.clipRect(scrollX + mPaddingLeft, scrollY + mPaddingTop,
//                    scrollX + mRight - mLeft - mPaddingRight,
//                    scrollY + mBottom - mTop - mPaddingBottom);
//            mGroupFlags &= ~CLIP_TO_PADDING_MASK;
//        }

        final boolean drawSelectorOnTop = mDrawSelectorOnTop;
        if (!drawSelectorOnTop) {
            drawSelector(canvas);
        }

        super.dispatchDraw(canvas);

        if (drawSelectorOnTop) {
            drawSelector(canvas);
        }

//        if (clipToPadding) {
//            canvas.restoreToCount(saveCount);
//            mGroupFlags |= CLIP_TO_PADDING_MASK;
//        }
    }
    
    /**
     * Indicates whether this view is in a state where the selector should be drawn. This will
     * happen if we have focus but are not in touch mode, or we are in the middle of displaying
     * the pressed state for an item.
     *
     * @return True if the selector should be shown
     */
    boolean shouldShowSelector() {
        return (hasFocus() && !isInTouchMode());// || touchModeDrawsInPressedState();
    }

    private void drawSelector(Canvas canvas) {
        if (!mSelectorRect.isEmpty()) {
            final Drawable selector = mSelector;
            selector.setBounds(mSelectorRect);
            selector.draw(canvas);
        }
    }

    /**
     * Controls whether the selection highlight drawable should be drawn on top of the item or
     * behind it.
     *
     * @param onTop If true, the selector will be drawn on the item it is highlighting. The default
     *        is false.
     *
     * @attr ref android.R.styleable#AbsListView_drawSelectorOnTop
     */
    public void setDrawSelectorOnTop(boolean onTop) {
        mDrawSelectorOnTop = onTop;
    }

    /**
     * Set a Drawable that should be used to highlight the currently selected item.
     *
     * @param resID A Drawable resource to use as the selection highlight.
     *
     * @attr ref android.R.styleable#AbsListView_listSelector
     */
    public void setSelector(int resID) {
        setSelector(getResources().getDrawable(resID));
    }

    public void setSelector(Drawable sel) {
        if (mSelector != null) {
            mSelector.setCallback(null);
            unscheduleDrawable(mSelector);
        }
        mSelector = sel;
        Rect padding = new Rect();
        sel.getPadding(padding);
        mSelectionLeftPadding = padding.left;
        mSelectionTopPadding = padding.top;
        mSelectionRightPadding = padding.right;
        mSelectionBottomPadding = padding.bottom;
        sel.setCallback(this);
        updateSelectorState();
    }

    void updateSelectorState() {
        if (mSelector != null) {
            if (shouldShowSelector()) {
                mSelector.setState(getDrawableState());
            } else {
                mSelector.setState(StateSet.NOTHING);
            }
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        // If the child view is enabled then do the default behavior.
        if (mIsChildViewEnabled) {
            // Common case
            return super.onCreateDrawableState(extraSpace);
        }

        // The selector uses this View's drawable state. The selected child view
        // is disabled, so we need to remove the enabled state from the drawable
        // states.
        final int enabledState = ENABLED_STATE_SET[0];

        // If we don't have any extra space, it will return one of the static state arrays,
        // and clearing the enabled state on those arrays is a bad thing!  If we specify
        // we need extra space, it will create+copy into a new array that safely mutable.
        int[] state = super.onCreateDrawableState(extraSpace + 1);
        int enabledPos = -1;
        for (int i = state.length - 1; i >= 0; i--) {
            if (state[i] == enabledState) {
                enabledPos = i;
                break;
            }
        }

        // Remove the enabled state
        if (enabledPos >= 0) {
            System.arraycopy(state, enabledPos + 1, state, enabledPos,
                    state.length - enabledPos - 1);
        }

        return state;
    }

    @Override
    public boolean verifyDrawable(Drawable dr) {
        return mSelector == dr || super.verifyDrawable(dr);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (mSelector != null) mSelector.jumpToCurrentState();
    }
    
    /**
     * Returns the selector {@link android.graphics.drawable.Drawable} that is used to draw the
     * selection in the list.
     *
     * @return the drawable used to display the selector
     */
    public Drawable getSelector() {
        return mSelector;
    }

	/**
	 * When set to a non-zero value, the cache color hint indicates that this
	 * list is always drawn on top of a solid, single-color, opaque background.
	 * 
	 * Zero means that what's behind this object is translucent (non solid) or
	 * is not made of a single color. This hint will not affect any existing
	 * background drawable set on this view ( typically set via
	 * {@link #setBackgroundDrawable(Drawable)}).
	 * 
	 * @param color
	 *            The background color
	 */
	public void setCacheColorHint(int color) {
		if (color != mCacheColorHint) {
			mCacheColorHint = color;
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				getChildAt(i).setDrawingCacheBackgroundColor(color);
			}
			// mRecycler.setCacheColorHint(color);
		}
	}

	/**
	 * When set to a non-zero value, the cache color hint indicates that this
	 * list is always drawn on top of a solid, single-color, opaque background
	 * 
	 * @return The cache color hint
	 */
	public int getCacheColorHint() {
		return mCacheColorHint;
	}

	/**
	 * Control how items are stretched to fill their space.
	 * 
	 * @param stretchMode
	 *            Either {@link #NO_STRETCH}, {@link #STRETCH_SPACING},
	 *            {@link #STRETCH_SPACING_UNIFORM}, or
	 *            {@link #STRETCH_COLUMN_WIDTH}.
	 * 
	 * @attr ref android.R.styleable#GridView_stretchMode
	 */
	public void setStretchMode(int stretchMode) {
		if (stretchMode != mStretchMode) {
			mStretchMode = stretchMode;
			requestLayoutIfNecessary();
		}
	}

	public int getStretchMode() {
		return mStretchMode;
	}

	/**
	 * Indicates whether the content of this view is pinned to, or stacked from,
	 * the bottom edge.
	 * 
	 * @return true if the content is stacked from the bottom edge, false
	 *         otherwise
	 */
	public boolean isStackFromBottom() {
		return mStackFromBottom;
	}

	/**
	 * When stack from bottom is set to true, the list fills its content
	 * starting from the bottom of the view.
	 * 
	 * @param stackFromBottom
	 *            true to pin the view's content to the bottom edge, false to
	 *            pin the view's content to the top edge
	 */
	public void setStackFromBottom(boolean stackFromBottom) {
		if (mStackFromBottom != stackFromBottom) {
			mStackFromBottom = stackFromBottom;
			requestLayoutIfNecessary();
		}
	}

	/**
	 * Set the gravity for this grid. Gravity describes how the child views are
	 * horizontally aligned. Defaults to Gravity.LEFT
	 * 
	 * @param gravity
	 *            the gravity to apply to this grid's children
	 * 
	 * @attr ref android.R.styleable#GridView_gravity
	 */
	public void setGravity(int gravity) {
		if (mGravity != gravity) {
			mGravity = gravity;
			requestLayoutIfNecessary();
		}
	}

	public int getGravity() {
		return mGravity;
	}

	/**
	 * Set the amount of horizontal (x) spacing to place between each item in
	 * the grid.
	 * 
	 * @param horizontalSpacing
	 *            The amount of horizontal space between items, in pixels.
	 * 
	 * @attr ref android.R.styleable#GridView_horizontalSpacing
	 */
	public void setHorizontalSpacing(int horizontalSpacing) {
		if (horizontalSpacing != mRequestedHorizontalSpacing) {
			mRequestedHorizontalSpacing = horizontalSpacing;
			requestLayoutIfNecessary();
		}
	}

	/**
	 * Returns the amount of horizontal spacing currently used between each item
	 * in the grid.
	 * 
	 * <p>
	 * This is only accurate for the current layout. If
	 * {@link #setHorizontalSpacing(int)} has been called but layout is not yet
	 * complete, this method may return a stale value. To get the horizontal
	 * spacing that was explicitly requested use
	 * {@link #getRequestedHorizontalSpacing()}.
	 * </p>
	 * 
	 * @return Current horizontal spacing between each item in pixels
	 * 
	 * @see #setHorizontalSpacing(int)
	 * @see #getRequestedHorizontalSpacing()
	 * 
	 * @attr ref android.R.styleable#GridView_horizontalSpacing
	 */
	public int getHorizontalSpacing() {
		return mHorizontalSpacing;
	}

	/**
	 * Set the amount of vertical (y) spacing to place between each item in the
	 * grid.
	 * 
	 * @param verticalSpacing
	 *            The amount of vertical space between items, in pixels.
	 * 
	 * @see #getVerticalSpacing()
	 * 
	 * @attr ref android.R.styleable#GridView_verticalSpacing
	 */
	public void setVerticalSpacing(int verticalSpacing) {
		if (verticalSpacing != mVerticalSpacing) {
			mVerticalSpacing = verticalSpacing;
			requestLayoutIfNecessary();
		}
	}

	/**
	 * Returns the amount of vertical spacing between each item in the grid.
	 * 
	 * @return The vertical spacing between items in pixels
	 * 
	 * @see #setVerticalSpacing(int)
	 * 
	 * @attr ref android.R.styleable#GridView_verticalSpacing
	 */
	public int getVerticalSpacing() {
		return mVerticalSpacing;
	}

	/**
	 * Set the width of columns in the grid.
	 * 
	 * @param columnWidth
	 *            The column width, in pixels.
	 * 
	 * @attr ref android.R.styleable#GridView_columnWidth
	 */
	public void setColumnWidth(int columnWidth) {
		if (columnWidth != mRequestedColumnWidth) {
			mRequestedColumnWidth = columnWidth;
			requestLayoutIfNecessary();
		}
	}

	/**
	 * Return the width of a column in the grid.
	 * 
	 * <p>
	 * This may not be valid yet if a layout is pending.
	 * </p>
	 * 
	 * @return The column width in pixels
	 * 
	 * @see #setColumnWidth(int)
	 * @see #getRequestedColumnWidth()
	 * 
	 * @attr ref android.R.styleable#GridView_columnWidth
	 */
	public int getColumnWidth() {
		return mColumnWidth;
	}

	/**
	 * Set the width of columns in the grid.
	 * 
	 * @param columnWidth
	 *            The column width, in pixels.
	 * 
	 * @attr ref android.R.styleable#GridView_columnWidth
	 */
	public void setColumnHeight(int columnHeight) {
		if (columnHeight != mRequestedColumnHeight) {
			mRequestedColumnHeight = columnHeight;
			requestLayoutIfNecessary();
		}
	}

	/**
	 * Return the width of a column in the grid.
	 * 
	 * <p>
	 * This may not be valid yet if a layout is pending.
	 * </p>
	 * 
	 * @return The column width in pixels
	 * 
	 * @see #setColumnWidth(int)
	 * @see #getRequestedColumnWidth()
	 * 
	 * @attr ref android.R.styleable#GridView_columnWidth
	 */
	public int getColumnHeight() {
		return mColumnHeight;
	}

	/**
	 * Set the number of columns in the grid
	 * 
	 * @param numColumns
	 *            The desired number of columns.
	 * 
	 * @attr ref android.R.styleable#GridView_numColumns
	 */
	public void setNumColumns(int numColumns) {
		if (numColumns != mRequestedNumColumns) {
			mRequestedNumColumns = numColumns;
			requestLayoutIfNecessary();
		}
	}

	/**
	 * Get the number of columns in the grid. Returns {@link #AUTO_FIT} if the
	 * Grid has never been laid out.
	 * 
	 * @attr ref android.R.styleable#GridView_numColumns
	 * 
	 * @see #setNumColumns(int)
	 */
	public int getNumColumns() {
		return mNumColumns;
	}

	void requestLayoutIfNecessary() {
		if (getChildCount() > 0) {
			resetList();
			requestLayout();
			invalidate();
		}
	}

	final RecycleBin mRecycler = new RecycleBin();

	class RecycleBin {
		private final SparseArray<View> mScrapHeap = new SparseArray<View>();

		public void put(int position, View v) {
			mScrapHeap.put(position, v);
		}

		View get(int position) {
			// System.out.print("Looking for " + position);
			View result = mScrapHeap.get(position);
			if (result != null) {
				// System.out.println(" HIT");
				mScrapHeap.delete(position);
			} else {
				// System.out.println(" MISS");
			}
			return result;
		}

		void clear() {
			final SparseArray<View> scrapHeap = mScrapHeap;
			final int count = scrapHeap.size();
			for (int i = 0; i < count; i++) {
				final View view = scrapHeap.valueAt(i);
				if (view != null) {
					removeDetachedView(view, true);
				}
			}
			scrapHeap.clear();
		}
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new GDGridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0);
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new GDGridView.LayoutParams(getContext(), attrs);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof GDGridView.LayoutParams;
	}

	/**
	 * AbsListView extends LayoutParams to provide a place to hold the view
	 * type.
	 */
	public static class LayoutParams extends ViewGroup.LayoutParams {
		/**
		 * View type for this view, as returned by
		 * {@link android.widget.Adapter#getItemViewType(int) }
		 */
		int viewType;

		/**
		 * When this boolean is set, the view has been added to the AbsListView
		 * at least once. It is used to know whether headers/footers have
		 * already been added to the list view and whether they should be
		 * treated as recycled views or not.
		 */
		boolean recycledHeaderFooter;

		/**
		 * When an AbsListView is measured with an AT_MOST measure spec, it
		 * needs to obtain children views to measure itself. When doing so, the
		 * children are not attached to the window, but put in the recycler
		 * which assumes they've been attached before. Setting this flag will
		 * force the reused view to be attached to the window rather than just
		 * attached to the parent.
		 */
		boolean forceAdd;

		/**
		 * The ID the view represents
		 */
		long itemId = -1;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(int w, int h) {
			super(w, h);
		}

		public LayoutParams(int w, int h, int viewType) {
			super(w, h);
			this.viewType = viewType;
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}
	}
}
