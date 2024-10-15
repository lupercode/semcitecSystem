package com.luanpereira.semcitecsystem.repositories;

import com.luanpereira.semcitecsystem.models.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    //@Query("SELECT t FROM Turma t WHERE t.curso.id = :cursoId")
    List<Classroom> findByCourseUuidOrderByName(UUID courseUuid);

    @Query(value = "SELECT NEXTVAL(classroom_seq)", nativeQuery = true)
    int getNextValFromSequence();
}
