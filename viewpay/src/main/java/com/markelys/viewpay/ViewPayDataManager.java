package com.markelys.viewpay;

import android.content.Context;

/**
 * Created by Herbert TOMBO on 24/01/2018.
 */

class ViewPayDataManager
{
    private String accountID ="";
    private String accessMessage ="";
    private int  userAge = 0;
    private String genre = "";
    private String hostID = "";
    private String cvID = "";
    private boolean isTablet=false;
    private String country = "";
    private String language = "";
    private String postalCode = "";
    private String latitude = "";
    private String longitude = "";
    private String categorie = "";
    private String serverUrl = "";

    public Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

    private Context appContext=null;

    private ViewPayDataManager()
    {}

    private static ViewPayDataManager INSTANCE = new ViewPayDataManager();

    public static ViewPayDataManager getInstance()
    {
        return INSTANCE;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getAccessMessage() {
        return accessMessage;
    }

    public void setAccessMessage(String accessMessage) {
        this.accessMessage = accessMessage;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getCvID() {
        return cvID;
    }

    public void setCvID(String cvID) {
        this.cvID = cvID;
    }

    public boolean isTablet() {
        return isTablet;
    }

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

}