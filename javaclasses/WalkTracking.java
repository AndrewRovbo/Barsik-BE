import java.time.LocalDateTime;

public class WalkTracking {
    private Integer id;
    private Integer bookingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String pathGeojson;
    private Integer distanceMeters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getPathGeojson() {
        return pathGeojson;
    }

    public void setPathGeojson(String pathGeojson) {
        this.pathGeojson = pathGeojson;
    }

    public Integer getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Integer distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
}