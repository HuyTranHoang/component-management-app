package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.CategoryDAO;
import vn.aptech.componentmanagementapp.dao.CategoryDAOImpl;
import vn.aptech.componentmanagementapp.model.Category;

import java.util.List;

public class CategoryService {
    private CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAOImpl();
    }

    public List<Category> getAllCategory() {

        return categoryDAO.getAll();
    }
  
    public Category getCategoryById(int categoryId) {
        return categoryDAO.getById(categoryId);
    }

    public void addCategory(Category category) {

        categoryDAO.add(category);
    }

    public void updateCategory(Category category) {

        categoryDAO.update(category);
    }

    public void deleteCategory(int categoryId) {

        categoryDAO.delete(categoryId);
    }

}
