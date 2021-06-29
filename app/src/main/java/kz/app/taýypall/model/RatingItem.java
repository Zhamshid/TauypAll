package kz.app.taýypall.model;


public class RatingItem {
    String rating,comment,user ;

    public RatingItem(String rating, String comment, String user) {
        this.rating = rating;
        this.comment = comment;
        this.user = user;
    }

    public RatingItem(){}

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }






}

