package com.example.android.bookdigger;

import java.util.List;

/**
 * BookDigger created by JCoupier on 07/06/2017.
 */

public class BooksModel {

    // Value of totalItems obtained from the JSON parsing
    private int mTotalItems;

    // The list of book items obtained from the JSON parsing
    private List<Book> mBooks;

    /**
     * Constructs a new {@link BooksModel} object.
     *
     * @param totalItems is the value of totalItems
     * @param books is the List of Book object
     */
    public BooksModel(int totalItems, List<Book> books){
        mTotalItems = totalItems;
        mBooks = books;
    }

    public int getTotalItems() {
        return mTotalItems;
    }

    public List<Book> getBooks() {
        return mBooks;
    }
}
