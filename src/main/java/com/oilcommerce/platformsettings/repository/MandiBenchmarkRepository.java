package com.oilcommerce.platformsettings.repository;

import com.oilcommerce.platformsettings.entity.MandiBenchmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MandiBenchmarkRepository extends JpaRepository<MandiBenchmark, UUID> {
    List<MandiBenchmark> findAllByOrderByCreatedAtAsc();
    Optional<MandiBenchmark> findByCommodity(String commodity);
}
