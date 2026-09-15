package com.oilcommerce.coupon.repository;
import com.oilcommerce.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; import java.util.UUID;
@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCodeAndActiveAndDeletedFalse(String code, boolean active);
}
