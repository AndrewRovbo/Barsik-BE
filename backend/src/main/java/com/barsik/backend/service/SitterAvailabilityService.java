package com.barsik.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barsik.backend.api.DTO.AvaliabilityDTO;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.SitterAvailability;
import com.barsik.backend.repository.SitterAvailabilityRepository;
import com.barsik.backend.repository.SitterRepository;

import jakarta.persistence.EntityNotFoundException;


@Service
public class SitterAvailabilityService {
    @Autowired private SitterAvailabilityRepository sitterAvailabilityRepository;
    @Autowired private SitterRepository sitterRepository;

    @Transactional
    public List<AvaliabilityDTO> findBySitterId(Long sitterId){
        List<AvaliabilityDTO> lst =  sitterAvailabilityRepository.findBySitterId1(sitterId);
        if(lst.isEmpty()){
            for(int day = 1; day <= 7; day++){
                lst.add(new AvaliabilityDTO(day,  null, null));
                Sitter s = sitterRepository.findById(sitterId).orElseThrow(() -> new EntityNotFoundException("Sitter not found"));
                SitterAvailability a = new SitterAvailability();
                a.setSitter(s);
                a.setDayOfWeek(day);
                a.setStartTime(null);
                a.setEndTime(null);
                sitterAvailabilityRepository.save(a);
            }
        }
        return lst;
    }
    
    @Transactional
    public void updateAvailability(Long sitterId, List<AvaliabilityDTO> request){
        
        for (int day = 1; day <= 7; day++) {
            int d = day;
            AvaliabilityDTO dto = request.stream()
                .filter(a -> a.getDayOfWeek() == d)
                .findFirst()
                .orElse(null);

            if (dto == null) {
                continue;//не на все дни нормик 
            }
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                if(dto.getStartTime() ==  dto.getEndTime()) continue;
                else throw new IllegalArgumentException("startTime и endTime должны быть либо оба null, либо оба заполнены");
            }
            sitterAvailabilityRepository.updateAvailability(sitterId, dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        }
    }

}
