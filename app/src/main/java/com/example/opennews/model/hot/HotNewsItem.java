package com.example.opennews.model.hot;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hot_news_items")
public class HotNewsItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int index;
    private String title;
    private String url;
    private String imageUrl;
    private int imageWidth;
    private int imageHeight;
    private String label;
    private String labelUrl;
    private String hotValue;
    //private String labelDesc;



    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelUrl() {
        return labelUrl;
    }

    public void setLabelUrl(String labelUrl) {
        this.labelUrl = labelUrl;
    }

    public String getHotValue() {
        return hotValue;
    }

    public void setHotValue(String hotValue) {
        this.hotValue = hotValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public String getLabelDesc() {
        return labelDesc;
    }

    public void setLabelDesc(String labelDesc) {
        this.labelDesc = labelDesc;
    }*/
}