package com.example.dogbreed;

/**
 * Created by celestine on 05/02/2018.
 */

public class DogBreedModel {
    private String name;
    private String image1;
    private String image2;
    private String image3;

    public DogBreedModel(String name,String image1,String image2,String image3){
        this.name=name;
        this.image1=image1;
        this.image2=image2;
        this.image3=image3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

}
