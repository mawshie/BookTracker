package org.example.booktracker.domain;

public enum Status {
    WANT_TO_READ("Want To Read"),
    READING("Reading"),
    ON_PAUSE("On Pause");

    private final String displayName;

    Status(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
    //Cart cart = new Cart();
    //cart.setStatus(Status.WANT_TO_READ);
    //cart.getStatus().getDisplayName();
}
