package com.example.crittercalls;

import org.tensorflow.lite.support.label.Category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ParcelableCategory implements Parcelable {
    private List<String> categoryLabels;
    private List<Float> categoryScores;

    public ParcelableCategory(List<Category> categories) {
        categoryLabels = new ArrayList<>();
        categoryScores = new ArrayList<>();

        for (Category category : categories) {
            categoryLabels.add(category.getLabel());
            categoryScores.add(category.getScore());
        }
    }
    public List<String> getCategoryLabels() {
        return categoryLabels;
    }
    public List<Float> getCategoryScores() {
        return categoryScores;
    }
    protected ParcelableCategory(Parcel in) {
        categoryLabels = in.createStringArrayList();
        categoryScores = new ArrayList<>();
        in.readList(categoryScores, Float.class.getClassLoader());
    }
    public static final Creator<ParcelableCategory> CREATOR = new Creator<ParcelableCategory>() {
        @Override
        public ParcelableCategory createFromParcel(Parcel in) {
            return new ParcelableCategory(in);
        }

        @Override
        public ParcelableCategory[] newArray(int size) {
            return new ParcelableCategory[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(categoryLabels);
        dest.writeList(categoryScores);
    }
}