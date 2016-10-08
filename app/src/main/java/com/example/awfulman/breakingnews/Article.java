package com.example.awfulman.breakingnews;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by mikhaillyapich on 01.10.16.
 */

public class Article implements Parcelable{

    private int image;
    private String title;
    private String text;
    private String date;

    protected Article(Parcel in) {
        image = in.readInt();
        title = in.readString();
        text = in.readString();
        date = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getDate() {
        return date;
    }

    public Article(int image, String title, String text, String date) {
        this.image = image;
        this.title = title;
        this.text = text;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(date);
    }
}
