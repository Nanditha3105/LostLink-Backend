package com.lostlink.backend.repository;

import com.lostlink.backend.entity.Claim;
import com.lostlink.backend.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByFinder_IdOrderByCreatedAtDesc(Long finderId);
    List<Claim> findByItem_Reporter_IdOrderByCreatedAtDesc(Long reporterId);
    boolean existsByItem_IdAndFinder_Id(Long itemId, Long finderId);
    boolean existsByItem_IdAndStatus(Long itemId, ClaimStatus status);
}