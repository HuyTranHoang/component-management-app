package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.CategoryDAO;
import vn.aptech.componentmanagementapp.dao.CategoryDAOImpl;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Supplier;

import java.util.List;

public class CategoryService {
    private CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAOImpl();
    }

    public List<Category> getAllCategory() {
        return categoryDAO.getAll();
    }

    public void addCategory(Category category){categoryDAO.add(category);}
}
