package com.couchdbdemo.nikola.couchdbdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String TAG = "HelloCouchDb";
    Manager manager;
    String dbname = "hello";
    Database database;
    String currentTimeString;
    Map<String, Object> docContent;
    Document document;
    String docID;
    Document retrievedDocument;
    Map<String, Object> updatedProperties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Local couchdb, CRUD example
        Log.d(TAG, "Begin Hello World App");
        createManager();
        createDatabase();
        getCurrentDate();
        createObjectForDocument();
        addContentToDocument();
        retrieveDocument();
        updateDocument();
        deleteDocument();
        Log.d(TAG, "End Hello World App");

    }

    private void createManager() {
        // create a manager
        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            Log.d (TAG, "Manager created");
        } catch (IOException e) {
            Log.e(TAG, "Cannot create manager object");
            return;
        }
    }

    private void createDatabase() {
        // create a name for the database and make sure the name is legal
        if (!Manager.isValidDatabaseName(dbname)) {
            Log.e(TAG, "Bad database name");
            return;
        }
        // create a new database
        try {
            database = manager.getDatabase(dbname);
            Log.d (TAG, "Database created");

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot get database");
            return;
        }
    }

    private void getCurrentDate() {
        // get the current date and time
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = GregorianCalendar.getInstance();
        currentTimeString = dateFormatter.format(calendar.getTime());
    }

    private void createObjectForDocument() {
        // create an object that contains data for a document
        docContent = new HashMap<String, Object>();
        docContent.put("message", "Hello Couchbase Lite");
        docContent.put("creationDate", currentTimeString);
        // display the data for the new document
        Log.d(TAG, "docContent=" + String.valueOf(docContent));
    }

    private void addContentToDocument() {
        // create an empty document
        document = database.createDocument();
        // add content to document and write the document to the database
        try {
            document.putProperties(docContent);
            Log.d (TAG, "Document written to database named " + dbname + " with ID = " + document.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }
    }

    private void retrieveDocument() {
        // save the ID of the new document
        docID = document.getId();
        // retrieve the document from the database
        retrievedDocument = database.getDocument(docID);
        // display the retrieved document
        Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
    }

    private void updateDocument() {
        // update the document
        updatedProperties = new HashMap<String, Object>();
        updatedProperties.putAll(retrievedDocument.getProperties());
        updatedProperties.put ("message", "We're having a heat wave!");
        updatedProperties.put("temperature", "95");
        try {
            retrievedDocument.putProperties(updatedProperties);
            Log.d(TAG, "updated retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
        } catch (CouchbaseLiteException e) {
            Log.e (TAG, "Cannot update document", e);
        }
    }

    private void deleteDocument() {
        // delete the document
        try {
            retrievedDocument.delete();
            Log.d (TAG, "Deleted document, deletion status = " + retrievedDocument.isDeleted());
        } catch (CouchbaseLiteException e) {
            Log.e (TAG, "Cannot delete document", e);
        }
    }

}
