package com.example.awfulman.breakingnews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArticleActivity extends AppCompatActivity implements OnTouchListener, ArticleFragment.OnFragmentInteractionListener {

    private final static String ARTICLES = "articles";
    private final static String POSITION = "position";
    private static final int STEP = 200;

    private ArrayList<Article> articles;
    private int position;
    private ImageView imgView;
    private TextView titleView;
    private TextView txtView;
    private int mBaseDist;
    private float mBaseRatio;
    private float mRatio = 1.0f;

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
        txtView.setTextSize(13 + mRatio);
        txtView.setOnTouchListener(this);

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


    int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getPointerCount() == 2) {
            int action = event.getAction();
            int pureaction = action & MotionEvent.ACTION_MASK;

            if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                mBaseDist = getDistance(event);
                mBaseRatio = mRatio;
            } else {
                float delta = (getDistance(event) - mBaseDist) / STEP;
                float multi = (float) Math.pow(2, delta);
                mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                txtView.setTextSize(mRatio + 13);
            }
        }
        return true;
    }
}
