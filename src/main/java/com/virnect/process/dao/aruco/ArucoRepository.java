package com.virnect.process.dao.aruco;

import com.virnect.process.domain.Aruco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArucoRepository extends JpaRepository<Aruco, Long>, ArucoCustomRepository {
    @Query(value = "SELECT * from aruco where content_uuid is null order by aruco_id asc limit 1", nativeQuery = true)
    Optional<Aruco> findByIsNullContentUUID();

    Optional<Aruco> findByContentUUID(String contentUUID);

    @Query(value = "select * from aruco where process_id = :processId", nativeQuery = true)
    Optional<Aruco> selectFromProcessId(Long processId);

    @Query(value = "SELECT * from aruco where content_uuid is null order by aruco_id asc limit 1", nativeQuery = true)
    Optional<Aruco> getAruco();
}
