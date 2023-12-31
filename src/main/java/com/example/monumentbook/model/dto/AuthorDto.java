package com.example.monumentbook.model.dto;

import com.example.monumentbook.model.Book;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class AuthorDto {
    private Integer id;
    private String name;
    private String biography;

    public AuthorDto(Integer id, String name, String biography) {
    }
}
