package com.example.android.bookdigger;

/**
 * BookDigger created by JCoupier on 07/06/2017.
 * {@link Book} represents a Book object.
 */
public class Book {

    /** Title of the book */
    private String mTitle;

    /** Author of the book */
    private String mAuthor;

    /** Url for the small thumbnail of the book */
    private String mSmallThumbnailUrl;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param title is the title of the book
     * @param author is the author of the book
     * @param smallThumbnailUrl is the url for the image of the book
     */
    public Book (String title, String author, String smallThumbnailUrl){
        mTitle = title;
        mAuthor = author;
        mSmallThumbnailUrl = smallThumbnailUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSmallThumbnailUrl() {
        return mSmallThumbnailUrl;
    }
}

