package com.oilcommerce.address.repository;
import com.oilcommerce.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(UUID userId);
    Optional<Address> findByUserIdAndDefaultAddressTrue(UUID userId);
}
