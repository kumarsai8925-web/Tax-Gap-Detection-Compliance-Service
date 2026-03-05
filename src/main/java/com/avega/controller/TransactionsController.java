package com.avega.controller;

import java.util.List;

import com.avega.domain.transaction.Transactions;

import com.avega.utils.dto.transaction.TransactionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.avega.service.transaction.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {

	private TransactionService service;

	public TransactionsController(TransactionService service) {
		this.service = service;
	}
	
	@PostMapping("/process")
	public ResponseEntity<String> processTransaction(@RequestBody List<TransactionRequest> records){
		service.uploadTransactions(records);
		return ResponseEntity.status(HttpStatus.OK).body("Transaction records saved");
	}

	@GetMapping("")
	public ResponseEntity<List<Transactions>> getAll(){
		return ResponseEntity.status(HttpStatus.OK).body(service.getAllTransactions());
	}
	
}
