package com.insight.service;

import com.insight.database.entity.Company;
import com.insight.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Cacheable(value = "companies")
    public List<Company> findAll(){
        return companyRepository.findAll();
    }

    @Cacheable(value = "company-id", key="#id")
    public Optional<Company> findById(Long id){
        return companyRepository.findById(id);
    }
}
