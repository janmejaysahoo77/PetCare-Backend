package com.petcare.modules.post.repository;

import com.petcare.modules.post.entity.LostPetReport;
import com.petcare.modules.post.entity.LostPetReport.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LostPetRepository extends JpaRepository<LostPetReport, UUID> {
    List<LostPetReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<LostPetReport> findByOwnerIdOrderByCreatedAtDesc(String ownerId);
}
