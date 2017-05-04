package es.upsa.mimo.musicfest.Model;

/**
 * Created by Javier on 25/04/2017.
 */

public class Event {
    private String title;
    private String id;
    private String type;
    private String lat;
    private String lng;
    private String img;
    private String web_url;
    private String city_name;
    private String venue_name;
    private String startDate;
    private String endDate;


    public Event() {
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", img='" + img + '\'' +
                ", web_url='" + web_url + '\'' +
                ", city_name='" + city_name + '\'' +
                ", venue_name='" + venue_name + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }

    public Event(String title, String id, String type, String lat, String lng, String img, String web_url, String city_name, String venue_name, String startDate, String endDate) {
        this.title = title;
        this.id  = id;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.img = img;
        this.web_url = web_url;
        this.city_name = city_name;
        this.venue_name = venue_name;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public void setVenue_name(String venue_name) {
        this.venue_name = venue_name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
