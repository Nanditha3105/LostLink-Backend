package com.lostlink.backend.service;

import com.lostlink.backend.dto.ClaimRequest;
import com.lostlink.backend.dto.ClaimResponse;
import com.lostlink.backend.entity.Claim;
import com.lostlink.backend.entity.ClaimStatus;
import com.lostlink.backend.entity.Item;
import com.lostlink.backend.entity.User;
import com.lostlink.backend.repository.ClaimRepository;
import com.lostlink.backend.repository.ItemRepository;
import com.lostlink.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ClaimService(ClaimRepository claimRepository, ItemRepository itemRepository,
                        UserRepository userRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ClaimResponse create(Long itemId, ClaimRequest request, String email) {
        User finder = user(email);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (item.isReturned()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This item has already been returned");
        }
        if (item.getReporter() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This older item has no linked reporter and cannot accept managed claims");
        }
        if (item.getReporter().getId().equals(finder.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot claim your own item");
        }
        if (claimRepository.existsByItem_IdAndFinder_Id(itemId, finder.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already submitted a claim for this item");
        }

        Claim claim = new Claim();
        claim.setItem(item);
        claim.setFinder(finder);
        claim.setFoundLocation(request.foundLocation().trim());
        claim.setFoundDate(request.foundDate());
        claim.setCircumstances(request.circumstances().trim());
        claim.setContactName(request.contactName().trim());
        claim.setContactEmail(request.contactEmail().trim().toLowerCase());
        claim.setContactPhone(request.contactPhone() == null ? null : request.contactPhone().trim());
        return toResponse(claimRepository.save(claim));
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> findMyClaims(String email) {
        User finder = user(email);
        return claimRepository.findByFinder_IdOrderByCreatedAtDesc(finder.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> findClaimsForMyReports(String email) {
        User reporter = user(email);
        return claimRepository.findByItem_Reporter_IdOrderByCreatedAtDesc(reporter.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ClaimResponse decide(Long claimId, ClaimStatus decision, String email) {
        Claim claim = claim(claimId);
        User reporter = user(email);
        if (claim.getItem().getReporter() == null
                || !claim.getItem().getReporter().getId().equals(reporter.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the item reporter can review this claim");
        }
        if (decision != ClaimStatus.ACCEPTED && decision != ClaimStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be Accepted or Rejected");
        }
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only pending claims can be reviewed");
        }
        if (decision == ClaimStatus.ACCEPTED
                && claimRepository.existsByItem_IdAndStatus(claim.getItem().getId(), ClaimStatus.ACCEPTED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another claim is already accepted for this item");
        }
        claim.setStatus(decision);
        return toResponse(claim);
    }

    @Transactional
    public ClaimResponse confirmReturn(Long claimId, String email) {
        Claim claim = claim(claimId);
        User participant = user(email);
        boolean isFinder = claim.getFinder().getId().equals(participant.getId());
        boolean isReporter = claim.getItem().getReporter() != null
                && claim.getItem().getReporter().getId().equals(participant.getId());
        if (!isFinder && !isReporter) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the finder and reporter can confirm return");
        }
        if (claim.getStatus() == ClaimStatus.RETURNED) {
            return toResponse(claim);
        }
        if (claim.getStatus() != ClaimStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only accepted claims can confirm a handover");
        }

        if (isFinder) {
            claim.setFinderConfirmedReturn(true);
        } else {
            claim.setReporterConfirmedReturn(true);
        }

        if (claim.isFinderConfirmedReturn() && claim.isReporterConfirmedReturn()) {
            claim.setStatus(ClaimStatus.RETURNED);
            claim.getItem().setReturned(true);
        }
        return toResponse(claim);
    }

    private User user(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
    }

    private Claim claim(Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getId(), claim.getItem().getId(), claim.getItem().getTitle(),
                claim.getFinder().getId(), claim.getFinder().getFullName(), claim.getStatus(),
                claim.getFoundLocation(), claim.getFoundDate(), claim.getCircumstances(),
                claim.getContactName(), claim.getContactEmail(), claim.getContactPhone(),
                claim.isFinderConfirmedReturn(), claim.isReporterConfirmedReturn(), claim.getCreatedAt());
    }
}