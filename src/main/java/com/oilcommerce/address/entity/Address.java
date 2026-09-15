package com.oilcommerce.address.entity;

import com.oilcommerce.common.BaseEntity;
import com.oilcommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column private String label;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false) private String phone;
    @Column(nullable = false) private String addressLine1;
    @Column private String addressLine2;
    @Column(nullable = false) private String city;
    @Column(nullable = false) private String state;
    @Column(nullable = false) private String postalCode;
    @Column(nullable = false) @Builder.Default private String country = "India";
    @Column(nullable = false) @Builder.Default private boolean defaultAddress = false;
}
