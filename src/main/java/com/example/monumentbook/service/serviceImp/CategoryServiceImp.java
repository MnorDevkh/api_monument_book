package com.example.monumentbook.service.serviceImp;

import com.example.monumentbook.model.Book;
import com.example.monumentbook.model.BookCategory;
import com.example.monumentbook.model.requests.CategoryRequest;
import com.example.monumentbook.model.responses.ApiResponse;
import com.example.monumentbook.model.responses.CategoryResponse;
import com.example.monumentbook.repository.CategoryRepository;
import com.example.monumentbook.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImp(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ResponseEntity<?> findAllCategory() {
        try {
            List<BookCategory> bookCategories = categoryRepository.findAll();
            List<CategoryResponse> categoryResponseList = new ArrayList<>();
            for(BookCategory bookCategory : bookCategories){
                CategoryResponse categoryResponse = CategoryResponse.builder()
                        .id(bookCategory.getId())
                        .name(bookCategory.getName())
                        .description(bookCategory.getDescription())
                        .build();
                categoryResponseList.add(categoryResponse);
            }
            return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                    .message("Category fetch success")
                    .status(HttpStatus.OK)
                    .payload(categoryResponseList)
                    .build());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<BookCategory>builder()
                            .message("Fail: Category not found")
                            .status(HttpStatus.NOT_FOUND)
                            .build());
        }

    }

    @Override
    public ResponseEntity<?> saveCategory(CategoryRequest category) {
        BookCategory categoryObj = null;
        categoryObj =  BookCategory.builder()
                .name(category.getName())
                .description(category.getDescription())
                .build();
        categoryRepository.save(categoryObj);

        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                        .message("susses")
                        .status(HttpStatus.OK)

                .build());
    }


}
