package com.barsik.backend.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sitters")
public class Sitter {
    
    @Id
    private Long userId;

    @Column(name = "experience_summary")
    private String experienceSummary;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "reviews_count")
    private Integer reviewsCount = 0;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public Sitter() {
    }

    public Sitter(Long userId, String experienceSummary, User user) {
        this.userId = userId;
        this.experienceSummary = experienceSummary;
        this.user = user;
    }

    public Sitter(User user) {
        this.user = user;
    }
    
    private void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }


    //@OneToMany(mappedBy = "sitter", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<SitterService> offeredServices;

    @OneToMany(mappedBy = "sitter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SitterAvailability> availability;

    //@OneToMany(mappedBy = "sitter", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Booking> bookingsTaken;

    //@OneToMany(mappedBy = "sitter", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Review> givenReviews;
}
