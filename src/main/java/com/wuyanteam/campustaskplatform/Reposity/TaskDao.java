package com.wuyanteam.campustaskplatform.Reposity;

import com.wuyanteam.campustaskplatform.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDao extends JpaRepository<Task, Integer> {

}
