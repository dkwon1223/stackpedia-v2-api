package com.dkwondev.stackpedia_v2_api.service;

import com.dkwondev.stackpedia_v2_api.model.entity.Category;
import com.dkwondev.stackpedia_v2_api.model.entity.Technology;
import com.dkwondev.stackpedia_v2_api.repository.CategoryRepository;
import com.dkwondev.stackpedia_v2_api.repository.TechnologyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class TechnologyServiceImpl implements TechnologyService {

    private final TechnologyRepository technologyRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<Technology> getAllTechnologies() {
        return technologyRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Technology getTechnologyById(Long id) {
        return unwrapTechnology(technologyRepository.findById(id));
    }

    @Override
    public Technology getTechnologyBySlug(String slug) {
        Technology technology = technologyRepository.findBySlug(slug);
        if (technology == null) {
            throw new EntityNotFoundException("Technology not found.");
        }
        return technology;
    }

    @Override
    public Technology createTechnology(Technology technology) {
        return technologyRepository.save(technology);
    }

    @Override
    public Technology updateTechnology(Long id, Technology technology) {
        return technologyRepository.findById(id)
                .map(existingTech -> {
                    existingTech.setName(technology.getName());
                    existingTech.setShortDescription(technology.getShortDescription());
                    existingTech.setDescription(technology.getDescription());
                    existingTech.setSlug(technology.getSlug());
                    existingTech.setWebsiteUrl(technology.getWebsiteUrl());
                    existingTech.setGithubUrl(technology.getGithubUrl());
                    existingTech.setDocumentationUrl(technology.getDocumentationUrl());
                    return technologyRepository.save(existingTech);
                })
                .orElseThrow(() -> new EntityNotFoundException("Technology with id:" + id + " does not exist"));
    }

    @Override
    public Technology patchTechnology(Long id, Map<String, Object> updates) {
        Technology existingTech = unwrapTechnology(technologyRepository.findById(id));

        updates.forEach((field, value) -> {
            switch (field) {
                case "name":
                    if (value instanceof String) {
                        existingTech.setName((String) value);
                    }
                    break;
                case "shortDescription":
                    if (value instanceof String) {
                        existingTech.setShortDescription((String) value);
                    }
                    break;
                case "description":
                    if (value instanceof String) {
                        existingTech.setDescription((String) value);
                    }
                    break;
                case "slug":
                    if (value instanceof String) {
                        existingTech.setSlug((String) value);
                    }
                    break;
                case "websiteUrl":
                    if (value instanceof String || value == null) {
                        existingTech.setWebsiteUrl((String) value);
                    }
                    break;
                case "githubUrl":
                    if (value instanceof String || value == null) {
                        existingTech.setGithubUrl((String) value);
                    }
                    break;
                case "documentationUrl":
                    if (value instanceof String || value == null) {
                        existingTech.setDocumentationUrl((String) value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Field '" + field + "' is not supported.");
            }
        });

        return technologyRepository.save(existingTech);
    }


    @Override
    public void deleteTechnology(Long id) {
        unwrapTechnology(technologyRepository.findById(id));
        technologyRepository.deleteById(id);
    }

    @Override
    public Technology addCategoryToTechnology(Long technologyId, Long categoryId) {
        Technology targetTechnology = unwrapTechnology(technologyRepository.findById(technologyId));
        Category targetCategory =  CategoryServiceImpl.unwrap(categoryRepository.findById(categoryId));
        targetTechnology.addCategory(targetCategory);
        return targetTechnology;
    }

    @Override
    public Technology removeCategoryFromTechnology(Long technologyId, Long categoryId) {
        Technology targetTechnology = unwrapTechnology(technologyRepository.findById(technologyId));
        Category targetCategory =  CategoryServiceImpl.unwrap(categoryRepository.findById(categoryId));
        targetTechnology.removeCategory(targetCategory);
        return targetTechnology;
    }

    @Override
    public Set<Technology> getTechnologiesByCategoryId(Long categoryId) {
        Category targetCategory =  CategoryServiceImpl.unwrap(categoryRepository.findById(categoryId));
        return targetCategory.getTechnologies();
    }

    @Override
    public Set<Category> getCategoriesByTechnologyId(Long technologyId) {
        Technology targetTechnology = unwrapTechnology(technologyRepository.findById(technologyId));
        return targetTechnology.getCategories();
    }

    public static Technology unwrapTechnology(Optional<Technology> technology) {
        if (technology.isPresent()) {
            return technology.get();
        } else {
            throw new EntityNotFoundException("Technology not found.");
        }
    }
}
