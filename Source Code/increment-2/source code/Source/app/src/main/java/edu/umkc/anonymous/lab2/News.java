package edu.umkc.anonymous.lab2;

public class News {



    /** Location of the earthquake */
    private String mWebtitle;

    /** Time of the earthquake */
   private String mDatetime;

    /** Website URL of the earthquake */
    private String mUrl;

    /**
     * Constructs a new {@link News} object.
     *
     * @param webTitle is the magnitude (size) of the earthquake
     * @param datetime is the location where the earthquake happened

     * @param url is the website URL to find more details about the earthquake
     */
    public News(String webTitle,String datetime , String url) {
        mWebtitle = webTitle;
        mDatetime = datetime;
        mUrl = url;
    }


    /**
     * Returns the location of the earthquake.
     */
    public String getmWebtitle() {
        return mWebtitle;
    }

    /**
     * Returns the time of the earthquake.
     */
    public String getTimedate() {
        return mDatetime;
    }

    /**
     * Returns the website URL to find more information about the earthquake.
     */
    public String getUrl() {
        return mUrl;
    }
}
