package Model;

public class Item {
    private String title,manufacturer,category,image;
    private double price;
    private int stockAmount;

    private Item() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(int stockAmount) {
        this.stockAmount = stockAmount;
    }

    public Item(String title, String manufacturer, String category, String image, double price,int stockAmount) {
        this.title = title;
        this.manufacturer = manufacturer;
        this.category = category;
        this.image = image;
        this.price = price;
        this.stockAmount = stockAmount;
    }
}
