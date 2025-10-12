package com.barsik.backend.entity;

import java.util.ArrayList;
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

@Entity
@Table(name = "owners")
public class Owner {
    @Id
    private Long userId;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets;

    public Owner(){};
        public Owner(User user) {
        this.user = user;
        this.pets = new ArrayList<>();
    }

   
    public Owner(User user, String aboutMe, Boolean isVerified) {
        this.user = user;
        this.aboutMe = aboutMe;
        this.isVerified = isVerified != null ? isVerified : false;
        this.pets = new ArrayList<>();
    }


    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }

    public Boolean getIsVerified() { return isVerified; }
    private void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }


    // Удобные методы для работы с коллекцией pets
    public void addPet(Pet pet) {
        pets.add(pet);
        pet.setOwner(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner)) return false;
        return userId != null && userId.equals(((Owner) o).getUserId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
