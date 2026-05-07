package com.kriterion.mapper;

import com.kriterion.dto.user.UserResponse;
import com.kriterion.entity.User;

public interface UserMapper {
    UserResponse toResponse(User user);
}
