package com.example.awfulman.breakingnews;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by AwfulMan on 26.09.16.
 */

public class Breaking_News extends Application{
    public ArrayList<Category> catList;
    public CharSequence[] posibleCategories;
    public Breaking_News(){
        super();
        catList = new ArrayList<>();
   //     catList.add(new Category("test1"));
//        catList.add(new Category("test2"));
        posibleCategories = new CharSequence[]{"test1","test2"};

    }
}
