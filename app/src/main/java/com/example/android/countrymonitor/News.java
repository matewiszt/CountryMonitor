package com.example.android.countrymonitor;

public class News {

    //Create a variable for the title of the News
    private String mTitle;

    //Create a variable for the section of the News
    private String mSection;

    //Create a variable for the URL of the News
    private String mWebUrl;

    //Create a variable for the publication date of the News
    private String mPublicationDate;

    /*
     * Public constructor for the News object
     * @param title: the title of the News
     * @param section: the section of the News
     * @param url: the URL of the News
     * @param publicationDate: the publication date of the News
     */

    public News(String title, String section, String url, String publicationDate) {

        mTitle = title;
        mSection = section;
        mWebUrl = url;
        mPublicationDate = publicationDate;
    }

    /*
     * Get the title of the News object
     * @return String
     */

    public String getTitle() {
        return mTitle;
    }

    /*
     * Get the section of the News object
     * @return String
     */

    public String getSection() {
        return mSection;
    }

    /*
     * Get the URL of the News object
     * @return String
     */

    public String getUrl() {
        return mWebUrl;
    }

    /*
     * Get the publication date of the News object
     * @return String
     */

    public String getPublicationDate() {
        return mPublicationDate;
    }

}
