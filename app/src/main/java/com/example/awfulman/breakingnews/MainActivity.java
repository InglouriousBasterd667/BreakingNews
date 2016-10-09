package com.example.awfulman.breakingnews;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements android.widget.TextView.OnClickListener,
        AddCategoryDialog.AddCategoryDialogListener{
    public static final String EXTRA_CATEGORY = "com.example.awfulman.breakingnews.category";
    private final static int REQUEST_CODE_TITLE = 0;

    private Breaking_News app;
    private ListView catListView;
    private CategoryAdapter categoryAdapter;
    private AddCategoryDialog addDial;
    private DataBaseHelper dbHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private int last_pos;

//    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBaseHelper(this);

        mSqLiteDatabase = dbHelper.getWritableDatabase();
        dbHelper.onCreate(mSqLiteDatabase);
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.DataBaseEntry.CATEGORY_COLUMN, "test1");
        values.put(DataBaseHelper.DataBaseEntry.DATE_COLUMN, "1996-10-13");
        values.put(DataBaseHelper.DataBaseEntry.IMG_COLUMN, R.drawable.rolling_stone);
        values.put(DataBaseHelper.DataBaseEntry.TEXT_COLUMN, "Some long really interesting article about Rolling Stone. And about dogs. And about good people");
        values.put(DataBaseHelper.DataBaseEntry.TITLE_COLUMN, "First Article");
        mSqLiteDatabase.insert(DataBaseHelper.DataBaseEntry.DATABASE_TABLE,null, values);

        values.put(DataBaseHelper.DataBaseEntry.CATEGORY_COLUMN, "test1");
        values.put(DataBaseHelper.DataBaseEntry.DATE_COLUMN, "1996-10-13");
        values.put(DataBaseHelper.DataBaseEntry.IMG_COLUMN, R.drawable.apple_ex);
        values.put(DataBaseHelper.DataBaseEntry.TEXT_COLUMN, "Story about apple. And about dogs. And about good people");
        values.put(DataBaseHelper.DataBaseEntry.TITLE_COLUMN, "Second Article");
        mSqLiteDatabase.insert(DataBaseHelper.DataBaseEntry.DATABASE_TABLE,null, values);


        values.put(DataBaseHelper.DataBaseEntry.CATEGORY_COLUMN, "test2");
        values.put(DataBaseHelper.DataBaseEntry.DATE_COLUMN, "1996-10-13");
        values.put(DataBaseHelper.DataBaseEntry.IMG_COLUMN, R.drawable.apple_ex);
        values.put(DataBaseHelper.DataBaseEntry.TEXT_COLUMN, "Story about apple. And about dogs. And about good people");
        values.put(DataBaseHelper.DataBaseEntry.TITLE_COLUMN, "Article for test2");
        mSqLiteDatabase.insert(DataBaseHelper.DataBaseEntry.DATABASE_TABLE,null, values);

        values.put(DataBaseHelper.DataBaseEntry.CATEGORY_COLUMN, "test1");
        values.put(DataBaseHelper.DataBaseEntry.DATE_COLUMN, "1996-10-13");
        values.put(DataBaseHelper.DataBaseEntry.IMG_COLUMN, R.drawable.apple_ex);
        values.put(DataBaseHelper.DataBaseEntry.TEXT_COLUMN, "Story about apple. And about dogs. And about good people");
        values.put(DataBaseHelper.DataBaseEntry.TITLE_COLUMN, "Third article");
        mSqLiteDatabase.insert(DataBaseHelper.DataBaseEntry.DATABASE_TABLE,null, values);
        addDial= new AddCategoryDialog();

        this.app = (Breaking_News)this.getApplication();

        catListView = (ListView) findViewById(R.id.listview);
        showCatList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return; }
        if (requestCode == REQUEST_CODE_TITLE) {
            if (data == null) {
                return; }
            app.catList.get(last_pos).setLast_article(NewsListActivity.getLastArticle(data));
            showCatList();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int pos = catListView.getPositionForView(v);
        String category = app.catList.get(pos).getCategory();
        last_pos = pos;
        startActivityForResult(NewsListActivity.newIntent(this, category), REQUEST_CODE_TITLE);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, ArrayList<Category> values) {
        app.catList = values;
        categoryAdapter.UpdateCategoryList(values);
    }

    private void showCatList() {
        //app.catList = new ArrayList<>();
        //app.catList.add(new Category("FirstCategory"));

        categoryAdapter = new CategoryAdapter(app.catList, this, mTouchListener);
        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(categoryAdapter);
    }

    public void playMusic(MenuItem item) {
        Intent intent = new Intent(this,PlayerActivity.class);
        startActivity(intent);
    }

    public boolean[] getCurrentCategories(ArrayList<Category> categories){
        boolean[] currentCategories = new boolean[app.posibleCategories.length];
        HashSet<String> setOfCategories = new HashSet();
        for(Category category : categories){
            setOfCategories.add(category.getCategory());
        }

        for(int i = 0; i < app.posibleCategories.length; i++){
            if ( setOfCategories.contains(app.posibleCategories[i]) )
                currentCategories[i] = true;
        }


        return currentCategories;
    }

    public void categoriesMenu(MenuItem item) {
        Bundle b = new Bundle();
        b.putCharSequenceArray("categories",app.posibleCategories);
        b.putBooleanArray("currentCategories", getCurrentCategories(app.catList));
        addDial.setArguments(b);
        addDial.show(getFragmentManager(),"dlg1");
    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(MainActivity.this).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            catListView.requestDisallowInterceptTouchEvent(true);
//                            mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        catListView.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(catListView, v);
                                        } else {
//                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            catListView.setEnabled(true);
                                        }
                                    }
                                });
                    }
                }
                mItemPressed = false;
                break;
                default:
                    return false;
            }
            return true;
        }
    };

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final ListView listview, View viewToRemove) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = categoryAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = catListView.getPositionForView(viewToRemove);
        app.catList.get(position).setCategory("");
        categoryAdapter.remove(categoryAdapter.getItem(position));


        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = categoryAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
//                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        catListView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
//                                    mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    catListView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }

}

