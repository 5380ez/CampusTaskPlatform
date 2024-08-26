package com.wuyanteam.campustaskplatform.Reposity;

import com.wuyanteam.campustaskplatform.entity.Vcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VcodeDao extends JpaRepository<Vcode,Integer> {
    Vcode findByCode(String code);
}