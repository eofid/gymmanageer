package com.gym.management.gymmanager.service;

import com.gym.management.gymmanager.model.Membership;
import com.gym.management.gymmanager.repository.MembershipRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
    private MembershipRepository membershipRepository;

    public Membership saveMembership(Membership membership) {
        return membershipRepository.save(membership);
    }

    public Membership getMembershipById(Long id) {
        return membershipRepository.findById(id).orElse(null);
    }

    public boolean deleteMembership(Long id) {
        if (membershipRepository.existsById(id)) {
            membershipRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
