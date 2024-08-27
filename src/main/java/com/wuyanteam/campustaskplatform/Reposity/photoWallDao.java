package com.wuyanteam.campustaskplatform.Reposity;

import com.wuyanteam.campustaskplatform.entity.photoWall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface photoWallDao extends JpaRepository<photoWall, Integer> {
    photoWall findById(int id);
    List<photoWall> findByUserId(int Id);
}