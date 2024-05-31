package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategory(int id);
    List<Category> getCategories();
    Category updateCategory(int id, CategoryDTO category);
    void deleteCategory(int id);
}
