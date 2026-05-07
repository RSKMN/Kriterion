package com.kriterion.service;

import com.kriterion.dto.category.CategoryResponse;
import com.kriterion.dto.category.CreateCategoryRequest;
import com.kriterion.dto.category.UpdateCategoryRequest;
import com.kriterion.entity.Category;
import com.kriterion.entity.User;
import com.kriterion.exception.BadRequestException;
import com.kriterion.exception.NotFoundException;
import com.kriterion.exception.UnauthorizedException;
import com.kriterion.repository.CategoryRepository;
import com.kriterion.repository.UserRepository;
import com.kriterion.security.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        
        List<Category> allCategories = new ArrayList<>();
        allCategories.addAll(categoryRepository.findByUserIsNull());
        
        if (userId != null) {
            allCategories.addAll(categoryRepository.findByUserId(userId));
        }
        
        return allCategories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        // Check duplicates across default categories
        if (categoryRepository.existsByNameIgnoreCaseAndUserIsNull(request.getName())) {
            throw new BadRequestException("A default category with this name already exists");
        }

        // Check duplicates across user's custom categories
        if (categoryRepository.existsByNameIgnoreCaseAndUserId(request.getName(), userId)) {
            throw new BadRequestException("You already have a category with this name");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = new Category();
        category.setUser(user);
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setIsDefault(false);

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (category.getUser() == null || Boolean.TRUE.equals(category.getIsDefault())) {
            throw new BadRequestException("Default categories cannot be modified");
        }

        if (!category.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only modify your own categories");
        }

        // Check duplicate name if name changed
        if (!category.getName().equalsIgnoreCase(request.getName())) {
            if (categoryRepository.existsByNameIgnoreCaseAndUserIsNull(request.getName())) {
                throw new BadRequestException("A default category with this name already exists");
            }
            if (categoryRepository.existsByNameIgnoreCaseAndUserId(request.getName(), userId)) {
                throw new BadRequestException("You already have a category with this name");
            }
        }

        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Long userId = AuthenticationUtil.getAuthenticatedUserIdAsLong();
        if (userId == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (category.getUser() == null || Boolean.TRUE.equals(category.getIsDefault())) {
            throw new BadRequestException("Default categories cannot be deleted");
        }

        if (!category.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own categories");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .color(category.getColor())
                .isDefault(category.getIsDefault())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
