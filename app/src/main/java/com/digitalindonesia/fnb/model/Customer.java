package com.digitalindonesia.fnb.model;

public class Customer {
    private int id; // Nanti jadi Primary Key Auto Increment di SQLite
    private String customerCode;
    private String fullName;
    private String phone;
    private String email;
    private String gender; // "Laki-laki" / "Perempuan"
    private String address;
    private String birthDate;
    private String photoPath; // Simpan path URI dari internal storage / drawable

    // Constructor untuk insert data baru
    public Customer(String fullName, String phone, String email, String gender, String address, String birthDate) {
        this.customerCode = "PLG-" + System.currentTimeMillis(); // Auto-generate sementara
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.birthDate = birthDate;
    }

    // Constructor lengkap untuk baca dari SQLite nanti
    public Customer(int id, String customerCode, String fullName, String phone, String email, String gender, String address, String birthDate, String photoPath) {
        this.id = id;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.birthDate = birthDate;
        this.photoPath = photoPath;
    }

    // Getters
    public int getId() { return id; }
    public String getCustomerCode() { return customerCode; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getBirthDate() { return birthDate; }
    public String getPhotoPath() { return photoPath; }

    // Setters (Opsional, gunakan jika butuh update data di memori)
    public void setId(int id) { this.id = id; }
}