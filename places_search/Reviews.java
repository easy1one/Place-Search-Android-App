package com.example.jeeweonlee.places_search;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

public class Reviews {
    private String author_url;
    private String profile_photo_url;
    private String author_name;
    private String rating;
    private String time; // numbers
    private String con_time; // formatted string
    private String text;

    public Reviews(String author_url, String profile_photo_url, String author_name, String rating, String time, String con_time, String text) {
        this.author_url = author_url;
        this.profile_photo_url = profile_photo_url;
        this.author_name = author_name;
        this.rating = rating;
        this.time = time;
        if(time.length()>0){ // gg
            this.con_time = converTime(time);
        }else{ // yelp
            this.con_time = con_time;
        }
        this.text = text;
    }

    private String converTime(String utc_time){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = new Date(1000L * Integer.parseInt(utc_time));
        return sdf.format(d);

    }
    private String convertTime(String time) {
        return "YET!";
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCon_time() {
        return con_time;
    }

    public void setCon_time(String con_time) {
        this.con_time = con_time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Comparator<Reviews> HiRatingComparator = new Comparator<Reviews>() {

        public int compare(Reviews s1, Reviews s2) {
            String review1 = s1.getRating();
            String review2 = s2.getRating();

            //ascending order
//            return review1.compareTo(review2);

            //descending order
            return review2.compareTo(review1);
        }
    };

    public static Comparator<Reviews> LowRatingComparator = new Comparator<Reviews>() {

        public int compare(Reviews s1, Reviews s2) {
            String review1 = s1.getRating();
            String review2 = s2.getRating();

            //ascending order
            return review1.compareTo(review2);

            //descending order
//            return review2.compareTo(review1);
        }
    };

    public static Comparator<Reviews> RecentComparator = new Comparator<Reviews>() {

        public int compare(Reviews s1, Reviews s2) {
            String review1 = "";
            String review2 = "";

            if(s1.getTime().length() == 0){ // have only converted time
                review1 = s1.getCon_time().replaceAll(" ","");
                review1 = review1.replaceAll(":","");
                review1 = review1.replaceAll("-","");

                review2 = s2.getCon_time().replaceAll(" ","");
                review2 = review2.replaceAll(":","");
                review2 = review2.replaceAll("-","");

            }else {
                review1 = s1.getTime();
                review2 = s2.getTime();
            }

            //descending order
            return review2.compareTo(review1);
        }
    };

    public static Comparator<Reviews> OldComparator = new Comparator<Reviews>() {

        public int compare(Reviews s1, Reviews s2) {
            String review1 = "";
            String review2 = "";

            if(s1.getTime().length() == 0){ // have only converted time
                review1 = s1.getCon_time().replaceAll(" ","");
                review1 = review1.replaceAll(":","");
                review1 = review1.replaceAll("-","");

                review2 = s2.getCon_time().replaceAll(" ","");
                review2 = review2.replaceAll(":","");
                review2 = review2.replaceAll("-","");

            }else {
                review1 = s1.getTime();
                review2 = s2.getTime();
            }

            //ascending order
            return review1.compareTo(review2);

        }
    };
}
