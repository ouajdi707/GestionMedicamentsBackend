package com.gestion.medicaments.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "cabinets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cabinet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200)
    private String adresse;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100)
    private String ville;

    @NotBlank(message = "Le numéro est obligatoire")
    @Size(max = 20)
    private String numero;

    @NotBlank(message = "L'email est obligatoire")
    @Size(max = 100)
    @Email(message = "L'email doit être valide")
    private String email;

    @NotNull(message = "La spécialité est obligatoire")
    @Enumerated(EnumType.STRING)
    private Specialite specialite;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medecin_id", nullable = false)
    private User medecin;

    // Availability fields
    @ElementCollection
    @CollectionTable(name = "cabinet_working_days", 
                    joinColumns = @JoinColumn(name = "cabinet_id"))
    @Column(name = "working_day")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> workingDays;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "appointment_duration")
    private Integer appointmentDuration = 30; // Duration in minutes

    @Column(name = "break_start")
    private LocalTime breakStart;

    @Column(name = "break_end")
    private LocalTime breakEnd;

    @OneToMany(mappedBy = "cabinet")
    private Set<Appointment> appointments;

    public Set<DayOfWeek> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Set<DayOfWeek> workingDays) {
        this.workingDays = workingDays;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getAppointmentDuration() {
        return appointmentDuration;
    }

    public void setAppointmentDuration(Integer appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
    }

    public LocalTime getBreakStart() {
        return breakStart;
    }

    public void setBreakStart(LocalTime breakStart) {
        this.breakStart = breakStart;
    }

    public LocalTime getBreakEnd() {
        return breakEnd;
    }

    public void setBreakEnd(LocalTime breakEnd) {
        this.breakEnd = breakEnd;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
} 