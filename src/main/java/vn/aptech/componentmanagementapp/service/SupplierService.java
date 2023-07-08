package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.SupplierDAO;
import vn.aptech.componentmanagementapp.dao.SupplierDAOImpl;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Supplier;

import java.util.List;

public class SupplierService {

    private SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new SupplierDAOImpl();
    }

    public List<Supplier> getAllSupplier() {
        return supplierDAO.getAll();
    }
}
