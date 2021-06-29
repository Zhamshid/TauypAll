package kz.app.taýypall.model;

public class ProfileItem {
    int edit_icon,icon_2;
    String textView;
    int icons;
    String number;

    public ProfileItem(int icons, int edit_icon,String textView, String number) {
        this.icons = icons;
        this.edit_icon = edit_icon;
        this.textView = textView;
        this.number = number;
    }

    public ProfileItem(int icon_2, int textView2) {
        this.icon_2 = icon_2;
        this.textView2 = textView2;
    }


    public ProfileItem(int icons, String textView, String number, int edit_icon) {
        this.icons = icons;
        this.textView = textView;
        this.number = number;
        this.edit_icon = edit_icon;
    }





    public int getIcon_2() {
        return icon_2;
    }

    public ProfileItem(int icons, int edit_icon, int icon_2, String textView, String number, int textView2) {
        this.icons = icons;
        this.edit_icon = edit_icon;
        this.icon_2 = icon_2;
        this.textView = textView;
        this.number = number;
        this.textView2 = textView2;
    }

    public int getTextView2() {
        return textView2;
    }

    int textView2;


    public int getIcons() {
        return icons;
    }

    public String getTextView() {
        return textView;
    }

    public String getNumber() {
        return number;
    }

    public void setEdit_icon(int edit_icon) {
        this.edit_icon = edit_icon;
    }

    public void setIcon_2(int icon_2) {
        this.icon_2 = icon_2;
    }

    public void setTextView(String textView) {
        this.textView = textView;
    }

    public void setIcons(int icons) {
        this.icons = icons;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setTextView2(int textView2) {
        this.textView2 = textView2;
    }

    public int getEdit_icon() {
        return edit_icon;
    }



}
