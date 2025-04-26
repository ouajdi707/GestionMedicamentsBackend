package com.gestion.medicaments.services;

import com.gestion.medicaments.models.Appointment;
import com.gestion.medicaments.models.Cabinet;
import com.gestion.medicaments.models.User;
import com.gestion.medicaments.repositories.AppointmentRepository;
import com.gestion.medicaments.repositories.CabinetRepository;
import com.gestion.medicaments.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private CabinetRepository cabinetRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<Appointment> getAppointmentsByCabinet(Long cabinetId) {
        return appointmentRepository.findByCabinetId(cabinetId);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public Appointment createAppointment(Long cabinetId, Long patientId, LocalDateTime dateTime, String notes) {
        Optional<Cabinet> cabinet = cabinetRepository.findById(cabinetId);
        Optional<User> patient = userRepository.findById(patientId);

        if (cabinet.isEmpty() || patient.isEmpty()) {
            throw new RuntimeException("Cabinet or patient not found");
        }

        if (!isTimeSlotAvailable(cabinetId, dateTime)) {
            throw new RuntimeException("This time slot is not available");
        }

        Appointment appointment = new Appointment();
        appointment.setCabinet(cabinet.get());
        appointment.setPatient(patient.get());
        appointment.setAppointmentDateTime(dateTime);
        appointment.setNotes(notes);
        
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointmentStatus(Long appointmentId, String status) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        appointment.get().setStatus(status);
        return appointmentRepository.save(appointment.get());
    }

    public List<LocalDateTime> getAvailableTimeSlots(Long cabinetId, LocalDate date) {
        Optional<Cabinet> cabinetOpt = cabinetRepository.findById(cabinetId);
        if (cabinetOpt.isEmpty()) {
            throw new RuntimeException("Cabinet not found");
        }

        Cabinet cabinet = cabinetOpt.get();
        
        // Check if the cabinet works on this day
        if (!cabinet.getWorkingDays().contains(date.getDayOfWeek())) {
            return new ArrayList<>();
        }

        List<LocalDateTime> allPossibleSlots = generateTimeSlots(cabinet, date);
        List<Appointment> existingAppointments = appointmentRepository.findByCabinetIdAndDateRange(
            cabinetId,
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        );

        // Remove already booked slots
        List<LocalDateTime> bookedTimes = existingAppointments.stream()
            .map(Appointment::getAppointmentDateTime)
            .collect(Collectors.toList());

        return allPossibleSlots.stream()
            .filter(slot -> !bookedTimes.contains(slot))
            .collect(Collectors.toList());
    }

    private List<LocalDateTime> generateTimeSlots(Cabinet cabinet, LocalDate date) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalTime currentTime = cabinet.getStartTime();
        LocalTime endTime = cabinet.getEndTime();
        int duration = cabinet.getAppointmentDuration();

        while (currentTime.plusMinutes(duration).isBefore(endTime) || 
               currentTime.plusMinutes(duration).equals(endTime)) {
            
            // Skip break time
            if (cabinet.getBreakStart() != null && cabinet.getBreakEnd() != null) {
                if (currentTime.isAfter(cabinet.getBreakStart().minusMinutes(1)) && 
                    currentTime.isBefore(cabinet.getBreakEnd())) {
                    currentTime = cabinet.getBreakEnd();
                    continue;
                }
            }

            slots.add(LocalDateTime.of(date, currentTime));
            currentTime = currentTime.plusMinutes(duration);
        }

        return slots;
    }

    private boolean isTimeSlotAvailable(Long cabinetId, LocalDateTime dateTime) {
        Optional<Cabinet> cabinet = cabinetRepository.findById(cabinetId);
        if (cabinet.isEmpty()) {
            return false;
        }

        // Check if the cabinet works on this day
        if (!cabinet.get().getWorkingDays().contains(dateTime.getDayOfWeek())) {
            return false;
        }

        // Check if the time is within working hours
        LocalTime time = dateTime.toLocalTime();
        if (time.isBefore(cabinet.get().getStartTime()) || 
            time.isAfter(cabinet.get().getEndTime())) {
            return false;
        }

        // Check if the time is during break
        if (cabinet.get().getBreakStart() != null && cabinet.get().getBreakEnd() != null) {
            if (time.isAfter(cabinet.get().getBreakStart().minusMinutes(1)) && 
                time.isBefore(cabinet.get().getBreakEnd())) {
                return false;
            }
        }

        // Check if there's no existing appointment at this time
        List<Appointment> existingAppointments = appointmentRepository.findByCabinetIdAndDateRange(
            cabinetId,
            dateTime,
            dateTime.plusMinutes(cabinet.get().getAppointmentDuration())
        );

        return existingAppointments.isEmpty();
    }
} 