import java.math.BigDecimal;

public class SitterService {
    private Integer sitterUserId;
    private Integer serviceId;
    private BigDecimal price;

    public Integer getSitterUserId() {
        return sitterUserId;
    }

    public void setSitterUserId(Integer sitterUserId) {
        this.sitterUserId = sitterUserId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}