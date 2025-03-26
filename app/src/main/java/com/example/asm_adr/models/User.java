package com.example.asm_adr.models;


public class User {
    private String email, password, fullName, birthday, sex;

    public User(String email, String password, String fullName, String birthday, String sex) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.birthday = birthday;
        this.sex = sex;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getBirthday() { return birthday; }
    public String getSex() { return sex; }
}
