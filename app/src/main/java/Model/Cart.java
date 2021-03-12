package Model;

public class Cart {
    private Item item;
    private String userID,id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cart(Item item, String userID) {
        this.item = item;
        this.userID = userID;
    }

    public Cart() {

    }
}
