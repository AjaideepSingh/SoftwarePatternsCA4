package Model;

public class Order {
    private Item item;
    private String userID,dateTime;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Order(Item item, String userID,String dateTime) {
        this.item = item;
        this.userID = userID;
        this.dateTime = dateTime;
    }

    public Order() {

    }
}
