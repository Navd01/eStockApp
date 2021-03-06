package com.Naveed.eStockApp.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Naveed.eStockApp.Beans.Company;
import com.Naveed.eStockApp.Beans.FinalStockList;
import com.Naveed.eStockApp.Beans.Stock;
import com.Naveed.eStockApp.Repository.CompanyRepository;
import com.Naveed.eStockApp.Repository.StockRepository;
import com.Naveed.eStockApp.Service.MapValidationErrorService;
import com.Naveed.eStockApp.exceptions.CompanyCodeException;

@RestController
@RequestMapping("/api/v1.0/market/stock")
public class StockController {
	
	@Autowired
	MapValidationErrorService mapValidationErrorService;
	
	@Autowired
	StockRepository stockRepo;
	
	@Autowired
	CompanyRepository companyRepo;
	
	@PostMapping("/add/{companyCode}")
	public ResponseEntity<?> addStock(@Valid @RequestBody Stock stock, @PathVariable String companyCode, BindingResult result){
		
		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap!=null) return errorMap;
		
		Company company = companyRepo.findByCompanyCode(companyCode);
		Stock receivedStock;
		if(company == null) {
			throw new CompanyCodeException("Company with code " + companyCode + " doesnt exist" );
		}else {
			stock.setCompany(company);
			stock.setCompanyCode(companyCode);
			company.setLatestStockPrice(stock.getStockPrice());
			receivedStock = stockRepo.save(stock);
			
			
		}
		return new ResponseEntity<Stock>(receivedStock, HttpStatus.CREATED);
	}
	
	@GetMapping("/get/{companyCode}/{startDate}/{endDate}")
	public ResponseEntity<?> getStockInRange(@PathVariable String companyCode, @PathVariable String startDate , @PathVariable String endDate){
		LocalDate startDate1 = LocalDate.parse(startDate);
		LocalDate endDate1 = LocalDate.parse(endDate);
		System.out.println();
		
		List<Stock> stockList = stockRepo.getByCompanyCodeAndCreateAtBetween(companyCode, startDate1, endDate1);
		if(stockList == null) {
			throw new CompanyCodeException("No Data found in  " + startDate1 + "-" + endDate1 +" range" );
		}
		//This method calculates the max, min, avg stockprices
		FinalStockList finalList = new FinalStockList();
		calculateStockPrices(stockList , finalList);
		
		return new ResponseEntity<FinalStockList>(finalList , HttpStatus.OK); 
	}

	private void calculateStockPrices(List<Stock> stockList, FinalStockList finalList) {
		List<Double> stockPrices = new ArrayList<>();
		for(Stock s : stockList) {
			stockPrices.add(s.getStockPrice());
		}
		System.out.println(stockPrices);
		
	if(stockPrices != null) {
		Collections.sort(stockPrices);
		Double min = stockPrices.get(0);
		Double max = stockPrices.get(stockPrices.size()-1);
		Double avg = ((max + min)/2);
		
		finalList.setStocks(stockList);
		finalList.setMinStockPrice(min);
		finalList.setMaxStockPrice(max);
		finalList.setAvgStockPrice(avg);
		
	}
	
		
	}

	

}
