package com.oilcommerce.user.mapper;

import com.oilcommerce.user.dto.UserDto;
import com.oilcommerce.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "isEmailVerified", source = "emailVerified")
    @Mapping(target = "isActive", source = "active")
    UserDto toDto(User user);
    List<UserDto> toDtoList(List<User> users);
}
