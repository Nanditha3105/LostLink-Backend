package com.lostlink.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "finder_id", nullable = false)
    @JsonIgnore
    private User finder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(nullable = false, length = 150)
    private String foundLocation;

    @Column(nullable = false)
    private LocalDate foundDate;

    @Column(nullable = false, length = 1000)
    private String circumstances;

    @Column(nullable = false, length = 100)
    private String contactName;

    @Column(nullable = false, length = 150)
    private String contactEmail;

    @Column(length = 30)
    private String contactPhone;

    @Column(nullable = false)
    private boolean finderConfirmedReturn;

    @Column(nullable = false)
    private boolean reporterConfirmedReturn;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public User getFinder() { return finder; }
    public void setFinder(User finder) { this.finder = finder; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public String getFoundLocation() { return foundLocation; }
    public void setFoundLocation(String foundLocation) { this.foundLocation = foundLocation; }
    public LocalDate getFoundDate() { return foundDate; }
    public void setFoundDate(LocalDate foundDate) { this.foundDate = foundDate; }
    public String getCircumstances() { return circumstances; }
    public void setCircumstances(String circumstances) { this.circumstances = circumstances; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public boolean isFinderConfirmedReturn() { return finderConfirmedReturn; }
    public void setFinderConfirmedReturn(boolean finderConfirmedReturn) { this.finderConfirmedReturn = finderConfirmedReturn; }
    public boolean isReporterConfirmedReturn() { return reporterConfirmedReturn; }
    public void setReporterConfirmedReturn(boolean reporterConfirmedReturn) { this.reporterConfirmedReturn = reporterConfirmedReturn; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}