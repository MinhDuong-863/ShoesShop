package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{
    private final CategoryRepository categoryRepository;
    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder().name(categoryDTO.getName()).build();
        return categoryRepository.save(category);
    }
    @Override
    public Category getCategory(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
    @Override
    @Transactional
    public Category updateCategory(int id, CategoryDTO categoryDTO) {
        Category existingCategory = getCategory(id);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }
    @Override
    @Transactional
    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }
}
