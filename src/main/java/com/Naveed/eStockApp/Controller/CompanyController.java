package com.Naveed.eStockApp.Controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Naveed.eStockApp.Beans.Company;
import com.Naveed.eStockApp.Repository.CompanyRepository;
import com.Naveed.eStockApp.Service.MapValidationErrorService;
import com.Naveed.eStockApp.exceptions.CompanyCodeException;

@RestController
@RequestMapping("/api/v1.0/market/company")
public class CompanyController {
	
	@Autowired
	CompanyRepository companyRepo;
	
	@Autowired
	MapValidationErrorService mapValidationErrorService;

	@PostMapping("/register")
	public ResponseEntity<?> newRegistration(@Valid @RequestBody Company registration , BindingResult result) {
		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap!=null) return errorMap;
		
		Company company = companyRepo.save(registration);
		return new ResponseEntity<Company>(company, HttpStatus.CREATED);
	}
	
	@GetMapping("/info/{companyCode}")
	public ResponseEntity<?> getCompanyDetails(@PathVariable String companyCode){
		
		Company company = companyRepo.findByCompanyCode(companyCode);
		
		if(company == null) {
			throw new CompanyCodeException("Company with code " + companyCode + " doesnt exist" );
		}
		
		return new ResponseEntity<Company>(company , HttpStatus.OK);
	}
	
	@GetMapping("/getall")
	public List<Company> getAllCompanies(){
		
		List<Company> companyList =  companyRepo.findAll();
		return companyList;
	}
	
	@DeleteMapping("/delete/{companyCode}")
	public ResponseEntity<?> deleteCompany(@PathVariable String companyCode){
		Company company = companyRepo.findByCompanyCode(companyCode);
		
		if(company == null) {
			throw new CompanyCodeException("Company with code " + companyCode + " doesnt exist" );
		}
		
		companyRepo.delete(company);
		return new ResponseEntity<String>("Company with Code: '"+companyCode+"' was deleted", HttpStatus.OK);
		
	}
}
