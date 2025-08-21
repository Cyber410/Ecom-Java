package com.ecommerce.project.service;

import com.ecommerce.project.excpetions.APIException;
import com.ecommerce.project.excpetions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

       Sort sort= sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        // this represent the page details
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sort);

        // we need to get page itself
        Page<Category> categories=categoryRepository.findAll(pageDetails);

        if(categories.isEmpty()) {
            throw new APIException("No categories found");
        }else{
            List<CategoryDTO> categoryDTOs= categories.stream()
                    .map(category->modelMapper.map(category, CategoryDTO.class))
                    .toList();
            CategoryResponse categoryResponse=new CategoryResponse();
            categoryResponse.setContent(categoryDTOs);
            categoryResponse.setPageNumber(categories.getNumber());
            categoryResponse.setPageSize(categories.getSize());
            categoryResponse.setTotalElements(categories.getTotalElements());
            categoryResponse.setTotalPages(categories.getTotalPages());
            categoryResponse.setLastPage(categories.isLast());
            return categoryResponse;
        }
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category1=categoryRepository.findByCategoryName(categoryDTO.getCategoryName());

        if(category1!=null){
            throw new APIException("Category with name"+ " "+categoryDTO.getCategoryName()+ " " + "already exists");
        }
        else {
            Category category2= modelMapper.map(categoryDTO,Category.class);
            categoryRepository.save(category2);
            return modelMapper.map(category2,CategoryDTO.class);
        }
    }

    @Override
    public CategoryDTO deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category","id",id));
        CategoryDTO categoryDTO=modelMapper.map(category,CategoryDTO.class);
        categoryRepository.delete(category);
        return categoryDTO;

    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category existingCategory = optionalCategory.get();
            existingCategory.setCategoryName(categoryDTO.getCategoryName());
            CategoryDTO updatedCategory=modelMapper.map(existingCategory,CategoryDTO.class);
            categoryRepository.save(existingCategory);
            return updatedCategory;
        }
        else{
            throw new ResourceNotFoundException("Category","id",categoryId);
        }
    }
}
