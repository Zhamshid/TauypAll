package kz.app.taýypall.model;

public class ProductItem {
    String Zogolovok, Baga, Opisanie , phone, City, SharedPrefPhone, StorageName,like;

    ProductItem(){}

    public ProductItem(String Zogolovok, String Baga, String Opisanie , String phone, String City, String SharedPrefPhone, String StorageName) {
        this.Zogolovok = Zogolovok;
        this.Baga = Baga;
        this.Opisanie = Opisanie;
        this.phone = phone;
        this.City = City;
        this.SharedPrefPhone = SharedPrefPhone;
        this.StorageName = StorageName;
    }


    public String getZogolovok() {
        return Zogolovok;
    }

    public void setZogolovok(String zogolovok) {
        Zogolovok = zogolovok;
    }

    public String getBaga() {
        return Baga;
    }

    public void setBaga(String baga) {
        Baga = baga;
    }

    public String getOpisanie() {
        return Opisanie;
    }

    public void setOpisanie(String opisanie) {
        Opisanie = opisanie;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }
    public String getSharedPrefPhone() {
        return SharedPrefPhone;
    }

    public void setSharedPrefPhone(String SharedPrefPhone) {
        this.SharedPrefPhone = SharedPrefPhone;
    }
    public String getStorageName() {
        return StorageName;
    }

    public void setStorageName(String StorageName) {
        this.StorageName = StorageName;
    }





}

