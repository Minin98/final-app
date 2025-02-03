package com.kh.dto;

public class ClassDTO {
    private int classNumber;
    private String title;
    private String description;
    private String category;
    private String createTime;
    private String updateTime;
    private int rate;
    private String thumbnail;
    private String uno;
    private String name;

    public ClassDTO(int classNumber, String title, String description, String category,
            String createTime, String updateTime, String thumbnail, int rate, String uno, String name) {
        this.classNumber = classNumber;
        this.title = title;
        this.description = description;
        this.category = category;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.thumbnail = thumbnail;
        this.rate = rate;
        this.uno = uno;
        this.name = name;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
    }

    public String getUno() {
        return uno;
    }

    public void setUno(String uno) {
        this.uno = uno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "ClassDTO [classNumber=" + classNumber + ", uno=" + uno + ", name=" + name + ", title=" + title +
                ", description=" + description + ", category=" + category + ", createTime=" + createTime +
                ", updateTime=" + updateTime + ", rate=" + rate + ", thumbnail=" + thumbnail + "]";
    }
}