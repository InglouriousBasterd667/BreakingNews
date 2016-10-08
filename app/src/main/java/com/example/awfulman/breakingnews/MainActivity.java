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
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements android.widget.CheckBox.OnCheckedChangeListener,
                                                                android.widget.TextView.OnClickListener, AddCategoryDialog.AddCategoryDialogListener{
    public static final String EXTRA_CATEGORY = "com.example.awfulman.breakingnews.category";
    private final static int REQUEST_CODE_TITLE = 0;

    private Breaking_News app;
    private ListView catListView;
    private CategoryAdapter categoryAdapter;
    private AddCategoryDialog addDial;
    private DataBaseHelper dbHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private int last_pos;
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos;
        try{
            pos = catListView.getPositionForView(buttonView);
        }
        catch (Exception e){
               pos = 0;
        }
        if (pos != ListView.INVALID_POSITION){
            Category c = app.catList.get(pos);
            c.setSelected(isChecked);
//            Log.d("HAHAHAHAHAHA",Integer.toString(catListView.getPositionForView(buttonView)));
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, ArrayList<Category> values) {
        app.catList = values;
        categoryAdapter.UpdateCategoryList(values);
    }

    private void showCatList() {
        //app.catList = new ArrayList<>();
        //app.catList.add(new Category("FirstCategory"));

        categoryAdapter = new CategoryAdapter(app.catList, this);
        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(categoryAdapter);
    }

    public void deleteCategory(MenuItem item) {
        ArrayList<Category> newList = new ArrayList<>();
        for (Category cat :app.catList) {
            if (!cat.isSelected()) {
                newList.add(cat);
            }
        }
        app.catList = newList;
        Log.d("GASDFASDFASDF", Integer.toString(app.catList.size()));
        categoryAdapter.UpdateCategoryList(newList);
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


}
