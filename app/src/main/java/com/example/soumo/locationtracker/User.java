package com.example.soumo.locationtracker;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };


    private String name;
    private String email;
    private String phoneNumber;
    private String uid;
    private String orgId;
    private String organizationName;
    private String role;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(com.example.soumo.locationtracker.User.class)
    }

    public User(String username, String email, String phoneNumber, String uid, String orgId, String organizationName, String role) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        this.organizationName = organizationName;
        this.orgId = orgId;
        this.role = role;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

     public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getRole(){ return role; }

    public void setRole(String role){ this.role = role; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.uid);
        dest.writeString(this.organizationName);
        dest.writeString(this.orgId);
        dest.writeString(this.role);

    }
    public User(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.phoneNumber = in.readString();
        this.uid = in.readString();
        this.organizationName = in.readString();
        this.orgId = in.readString();
        this.role = in.readString();

    }
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phonNumber='" + phoneNumber + '\'' +
                ", uid='" + uid + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", orgId='" + orgId + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}