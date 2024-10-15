package com.luanpereira.semcitecsystem.repositories;

import com.luanpereira.semcitecsystem.models.OfficialDocModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface OfficialDocRepository extends JpaRepository<OfficialDocModel, UUID> {
    OfficialDocModel findFirstByOrderByNumberDesc();
    OfficialDocModel findFirstByUuid(UUID uuid);
    @Query(value = "SELECT NEXTVAL(docnumber_seq)", nativeQuery = true)
    int getNextValFromSequence();
}
