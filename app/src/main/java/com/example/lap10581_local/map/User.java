package com.example.lap10581_local.map;

public class User {
    private String iD;
    private String userName;
    private String passWord;
    private String name;
    private String phone;

    public User(String iD, String userName, String passWord, String name, String phone) {
        this.iD = iD;
        this.userName = userName;
        this.passWord = passWord;
        this.name = name;
        this.phone = phone;
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
