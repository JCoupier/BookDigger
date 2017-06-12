package com.example.android.bookdigger;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * BookDigger created by JCoupier on 07/06/2017.
 * Loads a list of books by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class BooksLoader extends AsyncTaskLoader<List<Book>> {

    /** Tag for log messages */
    private static final String LOG_TAG = BooksLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /** User input */
    private String mSearchInput;

    /** Start index */
    private int mStartIndex;

    /**
     * Constructs a new {@link BooksLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     * @param searchInput is the input of the user
     * @param startIndex is the start index for the request
     */
    public BooksLoader(Context context, String url, String searchInput, int startIndex) {
        super(context);
        mUrl = url;
        mSearchInput = searchInput;
        mStartIndex = startIndex;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null || mSearchInput == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of books.
        return BooksUtils.fetchBookData(mUrl + mSearchInput + "&startIndex=" + mStartIndex);
    }
}
