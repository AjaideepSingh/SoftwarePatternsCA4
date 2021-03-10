package Model;

public class Review {
    private String review,userID,productTitle,rating;

    public Review() {

    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Review(String review, String userID, String productTitle, String rating) {
        this.review = review;
        this.userID = userID;
        this.productTitle = productTitle;
        this.rating = rating;
    }
}
