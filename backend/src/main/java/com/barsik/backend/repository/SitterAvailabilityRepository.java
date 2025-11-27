package com.barsik.backend.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.barsik.backend.api.DTO.AvaliabilityDTO;
import com.barsik.backend.entity.SitterAvailability;

@Repository
public interface SitterAvailabilityRepository extends JpaRepository<SitterAvailability, Long> {


    @Query("SELECT new com.barsik.backend.api.DTO.AvaliabilityDTO(a.dayOfWeek, a.startTime, a.endTime) " +
       "FROM SitterAvailability a " +
       "WHERE a.sitter.id = :sitterId " +
       "ORDER BY a.dayOfWeek ASC")
    List<AvaliabilityDTO> findBySitterId1(@Param("sitterId") Long sitterId);


    @Modifying
    @Query("UPDATE SitterAvailability a SET a.startTime = :startTime, a.endTime = :endTime WHERE a.sitter.id = :sitterId AND a.dayOfWeek = :dayOfWeek")
    int updateAvailability(@Param("sitterId") Long sitterId, @Param("dayOfWeek") Integer dayOfWeek, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

}
