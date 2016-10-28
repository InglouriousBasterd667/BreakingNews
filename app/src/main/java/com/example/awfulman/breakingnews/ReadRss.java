package com.example.awfulman.breakingnews;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by mikhaillyapich on 09.10.16.
 */

public class ReadRss extends AsyncTask<Void,Void,Void> {
    Context context;
    ProgressDialog progressDialog;
    String address = "http://www.sciencemag.org/rss/news_current.xml";
    URL url;
    public ReadRss(Context context){
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading feed");
    }
    @Override
    protected Void doInBackground(Void... params) {
        ProcessXml(getData(address));
        return null;
    }



    @Override
    protected void onPreExecute() {
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();

    }

    private void ProcessXml(Document data) {
        if (data != null){
            DataBaseHelper dbHelper = new DataBaseHelper(context);
            SQLiteDatabase mSqliteDatabase = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DataBaseHelper.DataBaseCategoriesEntry.CATEGORY_COLUMN, "test1");
            mSqliteDatabase.insert(DataBaseHelper.DataBaseCategoriesEntry.DATABASE_TABLE, null, cv);
            Element root = data.getDocumentElement();
            Node chanel = root.getChildNodes().item(1);
            NodeList items = chanel.getChildNodes();
            ContentValues categories = new ContentValues();
            for(int i = 0; i < items.getLength();i++){
                Node currentChild = items.item(i);

                if (currentChild.getNodeName().equalsIgnoreCase("item")){
                    NodeList itemschild = currentChild.getChildNodes();

                    ContentValues values = new ContentValues();
                    for (int j =0; j < itemschild.getLength(); j++){
                        Node current = itemschild.item(j);
                        if(current.getNodeName().equalsIgnoreCase("media:category")){
                            String txt = current.getTextContent();
                            if (!categories.containsKey(txt))
                                categories.put(DataBaseHelper.DataBaseCategoriesEntry.CATEGORY_COLUMN, txt);
                            values.put(DataBaseHelper.DataBaseNewsEntry.CATEGORY_COLUMN, txt);
                        }
                        else if (current.getNodeName().equalsIgnoreCase("title"))
                            values.put(DataBaseHelper.DataBaseNewsEntry.TITLE_COLUMN, current.getTextContent());
                        else if (current.getNodeName().equalsIgnoreCase("pubDate"))
                            values.put(DataBaseHelper.DataBaseNewsEntry.DATE_COLUMN, current.getTextContent());
                        else if (current.getNodeName().equalsIgnoreCase("description"))
                            values.put(DataBaseHelper.DataBaseNewsEntry.TEXT_COLUMN, current.getTextContent());
                        else if (current.getNodeName().equalsIgnoreCase("media:thumbnail")) {
                            String url = current.getAttributes().item(0).getTextContent();
                            values.put(DataBaseHelper.DataBaseNewsEntry.IMG_COLUMN, url);
                        }
                    }

                    mSqliteDatabase.insert(DataBaseHelper.DataBaseNewsEntry.DATABASE_TABLE, null, values);
                    mSqliteDatabase.insert(DataBaseHelper.DataBaseCategoriesEntry.DATABASE_TABLE, null, categories);

                }
            }
        }


    }

    public Document getData(String address){
        try {
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xmlDoc = documentBuilder.parse(inputStream);
            return xmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
