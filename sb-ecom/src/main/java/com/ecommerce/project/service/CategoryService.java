package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long id);

/* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */

/* <<<<<<<<<<  c3f8a15b-1972-47d5-af3d-b4bc0308b02e  >>>>>>>>>>> */
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
}
