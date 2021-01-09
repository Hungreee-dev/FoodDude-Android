package com.example.dubstep.Model;

import java.util.ArrayList;
import java.util.List;

public class FoodClass {
    String category;
    String imageLink;
    List<Menu> menuList;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void addMenuItem(Menu menu) {
        this.menuList.add(menu);
    }

    public FoodClass() {
        menuList = new ArrayList<Menu>();
    }
}
