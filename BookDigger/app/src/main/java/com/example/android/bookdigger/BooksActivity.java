package com.example.android.bookdigger;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * BookDigger created by JCoupier on 07/06/2017.
 */
public class BooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<BooksModel> {

    /** URL for book data from the Google API dataset */
    private static final String GOOGLEAPI_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    // User's search input
    private String searchInput;

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** TextView displaying pagination info */
    private TextView mResultsTextView;

    /** The startIndex for the query url */
    private int startIndex = 0;

    /** Number of items displayed in the list (default = 10) */
    private int itemsNumber = 10;

    /** Constant value for the book loader ID. */
    private static final int BOOK_LOADER_ID = 1;

    BooksModel booksModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find the emptyStateTextView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Find the resultsTextView
        mResultsTextView = (TextView) findViewById(R.id.item_range);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Find the editTextView
        final EditText searchField = (EditText) findViewById(R.id.search_edit_text);

        // Find the searchButton
        final ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);

        // Find the previousButton
        final ImageView previousButton = (ImageView) findViewById(R.id.previous);

        // Find the nextButton
        final ImageView nextButton = (ImageView) findViewById(R.id.next);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(BOOK_LOADER_ID, null, BooksActivity.this);

        searchButton.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick (View view) {

                    // Hide the keyboard when the searchButton is clicked on
                    hideKeyboard(BooksActivity.this);

                    // Reset the startIndex
                    startIndex = 0;
                    
                    // Get the Input text that the user typed
                    searchInput = searchField.getText().toString().trim();

                    // Get a reference to the ConnectivityManager to check state of network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

                    // Get details on the currently active default data network
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    // If there is a network connection, fetch data
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Restart the loader
                        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BooksActivity.this);
                    } else {
                        // Otherwise, display error
                        // First, hide loading indicator so error message will be visible
                        View loadingIndicator = findViewById(R.id.loading_indicator);
                        loadingIndicator.setVisibility(View.GONE);

                        // Clear the adapter of previous book data
                        mAdapter.clear();

                        // Update empty state with no connection error message
                        mEmptyStateTextView.setText(R.string.no_internet_connection);
                    }
                }
        });

        previousButton.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Prevent the startIndex to be negative and display a help message for the user
                if (startIndex == 0){
                    Toast.makeText(BooksActivity.this, "No previous page", Toast.LENGTH_SHORT).show();
                } else {

                    // Remove 10 to the startIndex
                    startIndex -= itemsNumber;

                    // Get a reference to the ConnectivityManager to check state of network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

                    // Get details on the currently active default data network
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    // If there is a network connection, fetch data
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Restart the loader
                        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BooksActivity.this);
                    } else {
                        // Otherwise, display error
                        // First, hide loading indicator so error message will be visible
                        View loadingIndicator = findViewById(R.id.loading_indicator);
                        loadingIndicator.setVisibility(View.GONE);

                        // Clear the adapter of previous book data
                        mAdapter.clear();

                        // Update empty state with no connection error message
                        mEmptyStateTextView.setText(R.string.no_internet_connection);
                    }
                }
            }
        });

        nextButton.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startIndex + itemsNumber >= booksModel.getTotalItems()){
                    Toast.makeText(BooksActivity.this, "No next page", Toast.LENGTH_SHORT).show();
                } else {

                    // Add 10 to the startIndex
                    startIndex += itemsNumber;

                    // Get a reference to the ConnectivityManager to check state of network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

                    // Get details on the currently active default data network
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    // If there is a network connection, fetch data
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Restart the loader
                        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BooksActivity.this);
                    } else {
                        // Otherwise, display error
                        // First, hide loading indicator so error message will be visible
                        View loadingIndicator = findViewById(R.id.loading_indicator);
                        loadingIndicator.setVisibility(View.GONE);

                        // Clear the adapter of previous book data
                        mAdapter.clear();

                        // Update empty state with no connection error message
                        mEmptyStateTextView.setText(R.string.no_internet_connection);
                    }
                }
            }
        });
    }

    @Override
    public Loader<BooksModel> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BooksLoader(this, GOOGLEAPI_REQUEST_URL, searchInput, startIndex);
    }

    @Override
    public void onLoadFinished(Loader<BooksModel> loader, BooksModel booksModel) {

        this.booksModel = booksModel;

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No corresponding book found."
        mEmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (booksModel != null && booksModel.getBooks() != null && !booksModel.getBooks().isEmpty()) {
            mAdapter.addAll(booksModel.getBooks());

            // Update the Results TextView in the UI with the index of the books displayed
            mResultsTextView.setText("Results " + startIndex + " to " + (startIndex + itemsNumber) + " out of approximately " + booksModel.getTotalItems() + " Books");
        }
    }

    @Override
    public void onLoaderReset(Loader<BooksModel> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    /** This method is used to hide the keyboard */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
