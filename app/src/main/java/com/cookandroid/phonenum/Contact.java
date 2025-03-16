package com.cookandroid.phonenum;

public class Contact {
    private String name;
    private String phone;

    public Contact(String nam, String phone) {
        this.name = nam;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}