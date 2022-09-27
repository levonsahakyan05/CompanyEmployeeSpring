package com.example.companyemployeespring.controller;

import com.example.companyemployeespring.entity.Company;
import com.example.companyemployeespring.entity.Employee;
import com.example.companyemployeespring.repository.CompanyRepository;
import com.example.companyemployeespring.repository.EmployeeRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Value("${company.employee.spring.images.folder}")
    private String folderPath;

    @GetMapping("/employees/add")
    public String addEmployeePage(ModelMap modelMap) {
        List<Company> companies = companyRepository.findAll();
        modelMap.addAttribute("companies", companies);
        return "addEmployee";
    }

    @PostMapping("/employees/add")
    public String addEmployee(@ModelAttribute Employee employee,
                              @RequestParam("employeeImage") MultipartFile file) throws IOException {
        if (!file.isEmpty() && file.getSize() > 0) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File newFile = new File(folderPath + File.separator + fileName);
            file.transferTo(newFile);
            employee.setProfilePic(fileName);
        }
        Optional<Company> byId = companyRepository.findById(employee.getCompany().getId());
        employeeRepository.save(employee);
        Company company = byId.get();
        company.setSize(company.getSize() + 1);
        companyRepository.save(company);
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String employees(ModelMap modelMap) {
        List<Company> companies = companyRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();
        modelMap.addAttribute("companies", companies);
        modelMap.addAttribute("employees", employees);
        return "employees";
    }

    @GetMapping(value = "/employees/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("fileName") String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(folderPath + File.separator + fileName);
        return IOUtils.toByteArray(inputStream);
    }

    @GetMapping("/employees/delete")
    public String delete(@RequestParam("id") int id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        Optional<Company> byId = companyRepository.findById(employee.get().getCompany().getId());
        Company company = byId.get();
        company.setSize(company.getSize() - 1);
        companyRepository.save(company);
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}
