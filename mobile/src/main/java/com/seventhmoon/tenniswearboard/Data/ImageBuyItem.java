package com.seventhmoon.tenniswearboard.Data;


import android.graphics.Bitmap;

public class ImageBuyItem {
    private Bitmap image;
    private String title;
    private boolean selected;
    private boolean purchased;

    public ImageBuyItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public boolean getPurchased()
    {
        return purchased;
    }

    public void setPurchased(boolean purchased)
    {
        this.purchased = purchased;
    }
}
