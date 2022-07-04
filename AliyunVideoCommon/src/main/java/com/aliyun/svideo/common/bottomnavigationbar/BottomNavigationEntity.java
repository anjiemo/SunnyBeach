package com.aliyun.svideo.common.bottomnavigationbar;

/**
 * 状态栏Item实体类
 */
public class BottomNavigationEntity {
    private String text;
    private int selectedIcon;
    private int unSelectIcon;
    private int badgeNum;

    //need text
    public BottomNavigationEntity(String text, int unSelectIcon, int selectedIcon) {
        this.text = text;
        this.unSelectIcon = unSelectIcon;
        this.selectedIcon = selectedIcon;
    }

    //need text
    public BottomNavigationEntity(String text, int unSelectIcon, int selectedIcon, int badgeNum) {
        this.text = text;
        this.unSelectIcon = unSelectIcon;
        this.selectedIcon = selectedIcon;
        this.badgeNum = badgeNum;
    }

    //do not need text
    public BottomNavigationEntity(int unSelectIcon, int selectedIcon) {
        this.unSelectIcon = unSelectIcon;
        this.selectedIcon = selectedIcon;
    }


    //do not need text
    public BottomNavigationEntity(int unSelectIcon, int selectedIcon, int badgeNum) {
        this.unSelectIcon = unSelectIcon;
        this.selectedIcon = selectedIcon;
        this.badgeNum = badgeNum;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(int selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public int getUnSelectIcon() {
        return unSelectIcon;
    }

    public void setUnSelectIcon(int unSelectIcon) {
        this.unSelectIcon = unSelectIcon;
    }

    public int getBadgeNum() {
        return badgeNum;
    }

    public void setBadgeNum(int badgeNum) {
        this.badgeNum = badgeNum;
    }
}
