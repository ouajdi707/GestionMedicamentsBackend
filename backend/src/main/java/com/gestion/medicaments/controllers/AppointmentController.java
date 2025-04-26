package com.gestion.medicaments.controllers;

import com.gestion.medicaments.models.Appointment;
import com.gestion.medicaments.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/cabinet/{cabinetId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByCabinet(@PathVariable Long cabinetId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByCabinet(cabinetId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    @GetMapping("/available-slots/{cabinetId}")
    public ResponseEntity<List<LocalDateTime>> getAvailableTimeSlots(
            @PathVariable Long cabinetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableTimeSlots(cabinetId, date));
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestParam Long cabinetId,
            @RequestParam Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(appointmentService.createAppointment(cabinetId, patientId, dateTime, notes));
    }

    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(appointmentId, status));
    }
} 