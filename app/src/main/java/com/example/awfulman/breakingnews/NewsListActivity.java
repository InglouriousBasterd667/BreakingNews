package com.example.awfulman.breakingnews;

import java.util.Collections;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

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

    final int MENU_SORT_JAVA = 0;
    final int MENU_SORT_NDK = 1;

    static {
        System.loadLibrary("sort");
    }

    public native int[] sort(int[] freqs, int size);

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

        String[] from = new String[] {DataBaseHelper.DataBaseNewsEntry.IMG_COLUMN,
                DataBaseHelper.DataBaseNewsEntry.TITLE_COLUMN, DataBaseHelper.DataBaseNewsEntry.TEXT_COLUMN};
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
        newsData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = scAdapter.getCursor();
                cursor.moveToPosition(position);
                int id_del = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.DataBaseNewsEntry._ID));
                db.delete_by_id(id_del);
                scAdapter.changeCursor(db.getDataByCategory());
                scAdapter.notifyDataSetChanged();
                return true;
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,MENU_SORT_JAVA,0, "Java sort");
        menu.add(0,MENU_SORT_NDK,0, "NDK sort");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case MENU_SORT_JAVA:
                sortWith(MENU_SORT_JAVA);
                break;
            case MENU_SORT_NDK:
                sortWith(MENU_SORT_NDK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Integer getWordFrequency(String word, String text){
        word = word.toLowerCase();
        text = text.toLowerCase();
        int count = 0;
        int i = text.indexOf(word);
        while (i >= 0){
            count++;
            i = text.indexOf(word, i + 1);
        }
        return count;
    }

    private ArrayList<Integer> prepareFrequencyArray(String word, Cursor cursor){
        Article article;
        ArrayList<Integer> wordFrequencies = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            article = getArticleFromCursor(cursor);
            wordFrequencies.add(getWordFrequency(word, article.getText()));
            cursor.moveToNext();
        }
        return wordFrequencies;
    }

    private void sortWith(final int sortType){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Sort by word frequency:");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Sort", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int btnID) {
                String word = input.getText().toString();
                if (word.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Word can't be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Cursor cursor = scAdapter.getCursor();
                ArrayList<Integer> freqArray = prepareFrequencyArray(word, cursor);
                int[] freqs = new int[freqArray.size()];
                int[] freqs_res = new int[freqArray.size()];
                int i = 0;

                for(Integer freq:freqArray)
                    freqs[i++] = freq;
                long time = 0;

                if (sortType == MENU_SORT_NDK){
                    time = System.nanoTime();
                    freqs_res = sort(freqs, freqArray.size());
                    time = System.nanoTime() - time;
                    Log.d("Sorting", "---------------------------------");
                    for(int j = 0; j < freqArray.size(); j++)
                        Log.d("Sorting", Integer.toString(freqs_res[j]));
                    Log.d("Sorting", "" + time);

                }
                if(sortType == MENU_SORT_JAVA){
                    time = System.nanoTime();
                    Collections.sort(freqArray);
                    time = System.nanoTime() - time;
                    Log.d("Sorting", "---------------------------------");
                    for(int j = 0; j < freqArray.size(); j++)
                        Log.d("Sorting", Integer.toString(freqArray.get(j)));
                    Log.d("Sorting", "" + time);

                }

                Toast.makeText(getApplicationContext(), "Gone " + time + " ms",Toast.LENGTH_SHORT).show();



            }
        });
        alert.show();

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
        String img = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DataBaseNewsEntry.IMG_COLUMN));
        last_title = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DataBaseNewsEntry.TITLE_COLUMN));
        String txt = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DataBaseNewsEntry.TEXT_COLUMN));
        String date = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DataBaseNewsEntry.DATE_COLUMN));
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
