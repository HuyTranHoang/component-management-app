package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.SupplierDAO;
import vn.aptech.componentmanagementapp.dao.SupplierDAOImpl;
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

    public Supplier getSupplierById(int supplierId) {
        return supplierDAO.getById(supplierId);
    }

    public void addSupplier(Supplier supplier) {

        supplierDAO.add(supplier);
    }

    public void updateSupplier(Supplier supplier) {

        supplierDAO.update(supplier);
    }

    public void deleteSupplier(int supplierId) {

        supplierDAO.delete(supplierId);
    }

}
