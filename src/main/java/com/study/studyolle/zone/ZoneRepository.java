package com.study.studyolle.zone;

import com.study.studyolle.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone,Long> {

    Zone findByCityAndProvince(String cityName, String provinceName);
}
