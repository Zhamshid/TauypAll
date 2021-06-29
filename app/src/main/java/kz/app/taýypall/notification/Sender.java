package kz.app.taýypall.notification;

public class Sender {

    public Sender(Date date, String to) {
        this.data = date;
        this.to = to;
    }

    public Date getDate() {
        return data;
    }

    public void setDate(Date date) {
        this.data = date;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date data;
    public  String to;

}
