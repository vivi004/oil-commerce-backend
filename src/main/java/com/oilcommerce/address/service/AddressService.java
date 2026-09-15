package com.oilcommerce.address.service;

import com.oilcommerce.address.dto.*; import com.oilcommerce.address.entity.Address;
import com.oilcommerce.address.repository.AddressRepository;
import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.user.entity.User;
import com.oilcommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;

@Service @RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository; private final UserRepository userRepository;

    public List<AddressDto> getAddresses(UUID userId) {
        return addressRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId)
            .stream().map(this::toDto).toList();
    }

    @Transactional
    public AddressDto createAddress(UUID userId, AddressRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
        if (req.isDefaultAddress()) clearDefault(userId);
        Address address = Address.builder().user(user).label(req.getLabel())
            .fullName(req.getFullName()).phone(req.getPhone())
            .addressLine1(req.getAddressLine1()).addressLine2(req.getAddressLine2())
            .city(req.getCity()).state(req.getState()).postalCode(req.getPostalCode())
            .country(req.getCountry() != null ? req.getCountry() : "India")
            .defaultAddress(req.isDefaultAddress()).build();
        return toDto(addressRepository.save(address));
    }

    @Transactional
    public AddressDto updateAddress(UUID userId, UUID id, AddressRequest req) {
        Address a = findOwned(userId, id);
        if (req.isDefaultAddress()) clearDefault(userId);
        a.setLabel(req.getLabel()); a.setFullName(req.getFullName()); a.setPhone(req.getPhone());
        a.setAddressLine1(req.getAddressLine1()); a.setAddressLine2(req.getAddressLine2());
        a.setCity(req.getCity()); a.setState(req.getState()); a.setPostalCode(req.getPostalCode());
        a.setDefaultAddress(req.isDefaultAddress());
        return toDto(addressRepository.save(a));
    }

    @Transactional
    public void deleteAddress(UUID userId, UUID id) {
        Address a = findOwned(userId, id); a.setDeleted(true); addressRepository.save(a);
    }

    @Transactional
    public AddressDto setDefault(UUID userId, UUID id) {
        clearDefault(userId);
        Address a = findOwned(userId, id); a.setDefaultAddress(true);
        return toDto(addressRepository.save(a));
    }

    private void clearDefault(UUID userId) {
        addressRepository.findByUserIdAndDefaultAddressTrue(userId).ifPresent(a -> {
            a.setDefaultAddress(false); addressRepository.save(a);
        });
    }

    private Address findOwned(UUID userId, UUID id) {
        Address a = addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address","id",id));
        if (!a.getUser().getId().equals(userId)) throw new ResourceNotFoundException("Address","id",id);
        return a;
    }

    private AddressDto toDto(Address a) {
        return AddressDto.builder().id(a.getId()).label(a.getLabel()).fullName(a.getFullName())
            .phone(a.getPhone()).addressLine1(a.getAddressLine1()).addressLine2(a.getAddressLine2())
            .city(a.getCity()).state(a.getState()).postalCode(a.getPostalCode())
            .country(a.getCountry()).isDefault(a.isDefaultAddress()).build();
    }
}
