package model;

public class Orders {
    private int id;
    private int id_customer;
    private int id_product;
    private int total_price;
    private int amount;

    public Orders()
    {

    }

    public Orders(int id, int id_customer, int id_product, int total_price, int amount)
    {
        this.id = id;
        this.id_customer = id_customer;
        this.id_product = id_product;
        this.total_price = total_price;
        this.amount = amount;
    }

    public Orders(int id_customer, int id_product, int total_price, int amount)
    {
        this.id_customer = id_customer;
        this.id_product = id_product;
        this.total_price = total_price;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_customer() {
        return id_customer;
    }

    public void setId_customer(int id_customer) {
        this.id_customer = id_customer;
    }

    public int getId_product() {
        return id_product;
    }

    public void setId_product(int id_product) {
        this.id_product = id_product;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
