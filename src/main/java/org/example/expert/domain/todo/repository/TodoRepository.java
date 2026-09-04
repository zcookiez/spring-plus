package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC, t.id DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query(value = "SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.weather = :weather ORDER BY t.modifiedAt DESC, t.id DESC",
           countQuery = "SELECT count(t.id) FROM Todo t WHERE t.weather = :weather")
    Page<Todo> findByWeather(@Param("weather") String weather, Pageable pageable);

    @Query(value = "SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE DATE(t.modifiedAt) >= :startDate AND DATE(t.modifiedAt) <= :endDate ORDER BY t.modifiedAt DESC, t.id DESC",
           countQuery = "SELECT count(t.id) FROM Todo t WHERE DATE(t.modifiedAt) >= :startDate AND DATE(t.modifiedAt) <= :endDate")
    Page<Todo> findByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    @Query(value = "SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.weather = :weather AND DATE(t.modifiedAt) >= :startDate AND DATE(t.modifiedAt) <= :endDate ORDER BY t.modifiedAt DESC, t.id DESC",
           countQuery = "SELECT count(t.id) FROM Todo t WHERE t.weather = :weather AND DATE(t.modifiedAt) >= :startDate AND DATE(t.modifiedAt) <= :endDate")
    Page<Todo> findByWeatherAndPeriod(@Param("weather") String weather, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
}
