package com.gestion.medicaments.repositories;

import com.gestion.medicaments.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCabinetId(Long cabinetId);
    
    List<Appointment> findByPatientId(Long patientId);
    
    @Query("SELECT a FROM Appointment a WHERE a.cabinet.id = :cabinetId AND a.appointmentDateTime BETWEEN :start AND :end")
    List<Appointment> findByCabinetIdAndDateRange(
        @Param("cabinetId") Long cabinetId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    List<Appointment> findByCabinetIdAndStatus(Long cabinetId, String status);
    
    List<Appointment> findByPatientIdAndStatus(Long patientId, String status);
} 