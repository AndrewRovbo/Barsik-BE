import java.time.LocalDateTime;

public class Availability {
    private Integer id;
    private Integer sitterUserId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSitterUserId() {
        return sitterUserId;
    }

    public void setSitterUserId(Integer sitterUserId) {
        this.sitterUserId = sitterUserId;
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
}