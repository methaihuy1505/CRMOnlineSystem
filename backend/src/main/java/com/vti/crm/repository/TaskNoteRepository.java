package com.vti.crm.repository;

import com.vti.crm.entity.TaskNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskNoteRepository extends JpaRepository<TaskNote, Integer> {
    
    List<TaskNote> findByTaskId(Integer taskId);

    @Modifying
    @Query("DELETE FROM TaskNote t WHERE t.task.id = :taskId")
    void deleteByTaskId(@Param("taskId") Integer taskId);
}
