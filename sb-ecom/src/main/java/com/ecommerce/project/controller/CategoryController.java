package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name="pageSize" ,defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_BY,required = false) String sortBy,
            @RequestParam(name="sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir
    ) {
        CategoryResponse categories = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>( savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id)  {

       CategoryDTO categoryDTO = categoryService.deleteCategory(id);
        return ResponseEntity.ok(categoryDTO);


    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory( @Valid @PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO) {

        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategoryDTO);

    }
}