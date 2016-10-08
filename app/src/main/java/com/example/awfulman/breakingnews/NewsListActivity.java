package com.example.awfulman.breakingnews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> , ArticleFragment.OnFragmentInteractionListener{

    public static final String EXTRA_CATEGORY = "com.example.awfulman.breakingnews.category";
    public static final String EXTRA_TITLE= "com.example.awfulman.breakingnews.title";
    public static final String TAG= "Lifecycle of NewsList";

    private ListView newsData;
    private NewsDB db;
    private String last_title = "";
    private FragmentManager myFragmentManager;
    private int positionInArray;
    private boolean landscapeOrientation = false;
    SimpleCursorAdapter scAdapter;

    private void setLastTitle(String title){
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        setResult(RESULT_OK, data);
    }


    private void createFragment(Article article){
        Fragment fragment = ArticleFragment.newInstance(article);
        FragmentTransaction fragmentTransaction = myFragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }

    public static Intent newIntent(Context context, String str){
        Intent i = new Intent(context, NewsListActivity.class);
        i.putExtra(EXTRA_CATEGORY,str);
        return i;
    }

    public static String getLastArticle(Intent result){
        return result.getStringExtra(EXTRA_TITLE);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        Log.d(TAG, "onCreate() called");

        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        myFragmentManager = getSupportFragmentManager();

        landscapeOrientation = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        db = new NewsDB(this);
        db.open(category);

        String[] from = new String[] {DataBaseHelper.DataBaseEntry.IMG_COLUMN,
                DataBaseHelper.DataBaseEntry.TITLE_COLUMN, DataBaseHelper.DataBaseEntry.TEXT_COLUMN};
        int[] to = new int[] { R.id.image, R.id.title, R.id.text };

        scAdapter = new SimpleCursorAdapter(this, R.layout.article_single_list_item, null, from, to, 0);
        newsData = (ListView) findViewById(R.id.news_listview);
        newsData.setAdapter(scAdapter);
        newsData.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = scAdapter.getCursor();
                cursor.moveToPosition(position);
                Article article = getArticleFromCursor(cursor);
                setLastTitle(article.getTitle());
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    createFragment(article);
                }
                else{
                    ArrayList<Article> articles = getArticles(cursor);
                    Intent intent = ArticleActivity.newIntent(NewsListActivity.this, positionInArray, articles);
                    startActivity(intent);
                }

            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }


    private ArrayList<Article> getArticles(Cursor cursor) {
        //ToDo: handle case when clicked last article
        ArrayList<Article> articles = new ArrayList<>();
        int count = 0;
        int toMove = cursor.getPosition() - 4;
        if (toMove > 0) {
            positionInArray = 4;
            cursor.moveToPosition(toMove);
        }
        else {
            positionInArray = toMove + 4;
            cursor.moveToFirst();
            toMove = 0;
        }
        while (!cursor.isAfterLast() && count < 10) {
            articles.add(getArticleFromCursor(cursor));
            cursor.moveToNext();
        }
        if (count < 10 && toMove != 0){
            cursor.moveToPosition(toMove - 1);
            while(!cursor.isBeforeFirst() && count < 10){
                articles.add(0,getArticleFromCursor(cursor));
                cursor.moveToPrevious();
                positionInArray++;
            }
        }
        return articles;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Article getArticleFromCursor(Cursor cursor){
        int img = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.DataBaseEntry.IMG_COLUMN));
        last_title = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DataBaseEntry.TITLE_COLUMN));
        String txt = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DataBaseEntry.TEXT_COLUMN));
        String date = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DataBaseEntry.DATE_COLUMN));
        return new Article(img, last_title, txt, date);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    static class MyCursorLoader extends CursorLoader {

        NewsDB db;
        public MyCursorLoader(Context context, NewsDB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getDataByCategory();
            return cursor;
        }

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause(){
        super.onStart();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onResume(){
        super.onStart();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onStop(){
        super.onStart();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onStart();
        Log.d(TAG, "onDestroy() called");
    }
    @Override
    public void onRestart(){
        super.onStart();
        Log.d(TAG, "onRestart() called");
    }
}
