package com.dkwondev.stackpedia_v2_api.model.dto.category;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String slug;
}
