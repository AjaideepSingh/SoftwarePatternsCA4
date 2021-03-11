package Model;

public class Order {
    private Item item;
    private String userID;

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

    public Order(Item item, String userID) {
        this.item = item;
        this.userID = userID;
    }

    public Order() {

    }
}
