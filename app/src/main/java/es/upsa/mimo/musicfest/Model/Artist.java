package es.upsa.mimo.musicfest.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Javier on 14/05/2017.
 */

public class Artist implements Parcelable {

    private String name;
    private String uri;
    private String id;

    public Artist() {
    }

    public Artist(String name, String uri, String id) {
        this.name = name;
        this.uri = uri;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    protected Artist(Parcel in) {
        name = in.readString();
        uri = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uri);
        dest.writeString(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}