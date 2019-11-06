package com.android.photocaptureviewer.data;

import java.io.Serializable;
import java.util.List;

public class ImageFolder implements Serializable {
    public String name;
    public String path;
    public ImageItem cover;
    public List<ImageItem> imageItems;

    @Override
    public boolean equals(Object o) {
        try {
            ImageFolder other = (ImageFolder) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

}
