package com.example.awfulman.breakingnews;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment implements View.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARTICLE = "article";
    private static final float STEP = 200;

    // TODO: Rename and change types of parameters
    private Article article;
    private TextView textView;
    private float ratio = 1.0f;
    private OnFragmentInteractionListener mListener;

    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArticleFragment.
     */
    public static ArticleFragment newInstance(Article article) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARTICLE, article);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            article = getArguments().getParcelable(ARTICLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.article_fragment, container, false);


        if (article != null) {
            TextView titleView = (TextView)v.findViewById(R.id.fragment_title);
            textView = (TextView)v.findViewById(R.id.fragment_text);
            ImageView imgView = (ImageView)v.findViewById(R.id.fragment_image);

            titleView.setText(article.getTitle());
            textView.setText(article.getText());
            textView.setTextSize(13);
            textView.setOnTouchListener(new View.OnTouchListener() {
                public float mBaseRatio;
                public int mBaseDist;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getPointerCount() == 2) {
                        int action = event.getAction();
                        int pureaction = action & MotionEvent.ACTION_MASK;
                        
                        if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                            mBaseDist = getDistance(event);
                            mBaseRatio = ratio;
                        } else {
                            float delta = (getDistance(event) - mBaseDist) / STEP;
                            float multi = (float) Math.pow(2, delta);
                            ratio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                            textView.setTextSize(ratio + 13);
                        }
                    }
                    return true;
                }

                private int getDistance(MotionEvent event) {
                    int dx = (int) (event.getX(0) - event.getX(1));
                    int dy = (int) (event.getY(0) - event.getY(1));
                    return (int) (Math.sqrt(dx * dx + dy * dy));
                }
            });

            //ToDo: change to setImageDrawable
            imgView.setImageResource(article.getImage());
        }
        return v;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
