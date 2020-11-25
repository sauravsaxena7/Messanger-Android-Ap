package com.jlcsoftware.bloodbankcommunity.Models;

public class User_Details_Models {


    public String username,userId,first_name,last_name,country_name,country_code,email
            ,phone_number,current_address,weight,work_as,blood_group,gender,
            img_uri,dob_month,dob_date,dob_year,followers,following,verify_user,verify_email;

    public User_Details_Models(String username, String userId, String first_name, String last_name,
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
}
