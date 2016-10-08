package com.example.awfulman.breakingnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by AwfulMan on 26.09.16.
 */
class Category{
    private String category;
    private String last_article = "";
    private boolean selected = false;

    public Category(String category) {
        this.category = category;

    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLast_article() {
        return last_article;
    }

    public void setLast_article(String last_article) {
        this.last_article = last_article;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}

public class CategoryAdapter extends ArrayAdapter<Category> {

    private Context context;
    private List<Category> categoryList;

    HashMap<Category, Integer> mIdMap = new HashMap();
    View.OnTouchListener mTouchListener;

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

    public CategoryAdapter(List<Category> categoryList, Context context, View.OnTouchListener touchListener) {
        super(context,R.layout.single_list_item,categoryList);
        this.categoryList = categoryList;
        this.context = context;
        mTouchListener = touchListener;
        for (int i = 0; i < categoryList.size(); ++i) {
            mIdMap.put(categoryList.get(i), i);
        }
    }

    private static class CategoryHolder{
        public TextView cat;
        public TextView last_article;
    }

    public void UpdateCategoryList(List<Category> newlist) {
        categoryList.clear();
        categoryList.addAll(newlist);
        for (int i = 0; i < categoryList.size(); ++i) {
            mIdMap.put(categoryList.get(i), i);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        Category item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CategoryHolder catHold = new CategoryHolder();
//        if (convertView == null){
            LayoutInflater infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = infl.inflate(R.layout.single_list_item, null);
            v.setOnTouchListener(mTouchListener);

            catHold.cat = (TextView) v.findViewById(R.id.category);
            catHold.last_article = (TextView) v.findViewById(R.id.last_article);

            catHold.cat.setOnClickListener((MainActivity)context);
        //convertView.setTag(catHold);
//        }else{
//            catHold = (CategoryHolder) v.getTag();
//        }
        Category c = categoryList.get(position);
        catHold.cat.setText(c.getCategory());
        catHold.last_article.setText(c.getLast_article());
        return v;
    }

}
