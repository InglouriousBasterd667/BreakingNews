package com.example.awfulman.breakingnews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArticleActivity extends AppCompatActivity implements ArticleFragment.OnFragmentInteractionListener{

    private final static String ARTICLES = "articles";
    private final static String POSITION = "position";

    private ArrayList<Article> articles;
    private int position;
    private ImageView imgView;
    private TextView titleView;
    private TextView txtView;

    public static Intent newIntent(Context context, int position, ArrayList<Article> articles){
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putParcelableArrayListExtra(ARTICLES, articles);
        intent.putExtra(POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        articles = getIntent().getParcelableArrayListExtra(ARTICLES);
        position = getIntent().getIntExtra(POSITION,0);

        imgView = (ImageView)findViewById(R.id.fragment_image);
        titleView = (TextView)findViewById(R.id.fragment_title);
        txtView = (TextView)findViewById(R.id.fragment_text);
        final Button prevBtn = (Button)findViewById(R.id.button_prev);
        final Button nextBtn = (Button)findViewById(R.id.button_next);

        if (position == 0)
            prevBtn.setVisibility(View.GONE);
        if (position == articles.size() - 1)
            nextBtn.setVisibility(View.GONE);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position > 0) {
                    fillArticle(--position);
                    nextBtn.setVisibility(View.VISIBLE);
                }
                if (position == 0)
                    prevBtn.setVisibility(View.GONE);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < articles.size() - 1){
                    fillArticle(++position);
                    prevBtn.setVisibility(View.VISIBLE);
                }
                if (position == articles.size() - 1)
                    nextBtn.setVisibility(View.GONE);
            }
        });
        fillArticle(position);
    }

    private void fillArticle(int position) {
        Article article = articles.get(position);
        imgView.setImageResource(article.getImage());
        titleView.setText(article.getTitle());
        txtView.setText(article.getText());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
