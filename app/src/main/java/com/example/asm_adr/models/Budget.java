package com.example.asm_adr.models;

public class Budget {
    private int id;
    private String category;
    private String note;
    private double amount;
    private String date;
    private String userEmail; // Khóa ngoại liên kết với User

    // Constructor đầy đủ
    public Budget(int id, String category, String note, double amount, String date, String userEmail) {
        this.id = id;
        this.category = category;
        this.note = note;
        this.amount = amount;
        this.date = date;
        this.userEmail = userEmail;
    }

    // Constructor không có id
    public Budget(String category, String note, double amount, String date, String userEmail) {
        this.category = category;
        this.note = note;
        this.amount = amount;
        this.date = date;
        this.userEmail = userEmail;
    }

    // Getter và Setter cho các trường cũ
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // Getter và Setter cho userEmail
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", note='" + note + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}