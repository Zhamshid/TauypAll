package kz.app.taýypall.model;


public class UserItem {
    public String getName() {
        return name;
    }

    public UserItem(){}

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    String name,phone,city,password ;

    public UserItem(String name, String phone, String city,String password) {
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.password = password;
    }

    public UserItem(String upd_name) {
        this.name = upd_name;
    }


}

