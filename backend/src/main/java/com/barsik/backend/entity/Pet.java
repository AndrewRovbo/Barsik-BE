package com.barsik.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pets")
public class Pet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private Owner owner;

    private String name;
    private String type;
    private String breed;
    private Byte age;
    private String gender;
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;
    
    public Pet(){};

    public Pet(Long id, Owner owner, String name, String type, String breed, Byte age, String gender, String description, String photoUrl) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.description = description;
        this.photoUrl = photoUrl;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }
    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Byte getAge() {
        return age;
    }

    public void setAge(Byte age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet)) return false;
        return id != null && id.equals(((Pet) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
}
