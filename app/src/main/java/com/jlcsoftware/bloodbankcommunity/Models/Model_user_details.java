package com.jlcsoftware.bloodbankcommunity.Models;



public class Model_user_details {

    public String username,userId,first_name,last_name,country_name,country_code,email
            ,phone_number,current_address,weight,work_as,blood_group,gender,
            img_uri,dob_month,dob_date,dob_year,followers,following,verify_user,verify_email;


    public Model_user_details() {
    }





    public Model_user_details(String username, String userId, String first_name, String last_name,
                              String country_name, String country_code, String email, String phone_number,
                              String current_address, String weight, String work_as, String blood_group,
                              String gender, String img_uri, String dob_month, String dob_date,
                              String dob_year, String followers, String following, String verify_user, String verify_email) {




        this.username = username;
        this.userId = userId;
        this.first_name = first_name;
        this.last_name = last_name;
        this.country_name = country_name;
        this.country_code = country_code;
        this.email = email;
        this.phone_number = phone_number;
        this.current_address = current_address;
        this.weight = weight;
        this.work_as = work_as;
        this.blood_group = blood_group;
        this.gender = gender;
        this.img_uri = img_uri;
        this.dob_month = dob_month;
        this.dob_date = dob_date;
        this.dob_year = dob_year;
        this.followers = followers;
        this.following = following;
        this.verify_user = verify_user;
        this.verify_email = verify_email;

}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setCurrent_address(String current_address) {
        this.current_address = current_address;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setWork_as(String work_as) {
        this.work_as = work_as;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    public void setDob_month(String dob_month) {
        this.dob_month = dob_month;
    }

    public void setDob_date(String dob_date) {
        this.dob_date = dob_date;
    }

    public void setDob_year(String dob_year) {
        this.dob_year = dob_year;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public void setVerify_user(String verify_user) {
        this.verify_user = verify_user;
    }

    public void setVerify_email(String verify_email) {
        this.verify_email = verify_email;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getCurrent_address() {
        return current_address;
    }

    public String getWeight() {
        return weight;
    }

    public String getWork_as() {
        return work_as;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getGender() {
        return gender;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public String getDob_month() {
        return dob_month;
    }

    public String getDob_date() {
        return dob_date;
    }

    public String getDob_year() {
        return dob_year;
    }

    public String getFollowers() {
        return followers;
    }

    public String getFollowing() {
        return following;
    }

    public String getVerify_user() {
        return verify_user;
    }

    public String getVerify_email() {
        return verify_email;
    }
}
