package com.imaginecurve.prepaidCard.service;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imaginecurve.prepaidCard.implementation.CardAccountShortenedBalance;
import com.imaginecurve.prepaidCard.implementation.CardAccountsManager;
import com.imaginecurve.prepaidCard.implementation.Transaction;


@RestController
@RequestMapping("/card")
@EnableAutoConfiguration
public class Application {
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
    
    /*
     * Using requestMapping instaed of get/put/post everywhere so that 
     * it is easier to call from browser 
     *
     */
    @RequestMapping(value = "/create")
	public Long createCard() {
		return CardAccountsManager.INSTANCE.createCard();
	}
    
    @RequestMapping(value = "/{id}/topUp")
	public Transaction topUp (@PathVariable Long id,
			@RequestParam Double amount) {
    	return CardAccountsManager.INSTANCE.topUp(id, amount);
	}
	
    @RequestMapping(value = "/{id}/authorize")
	public Transaction authorize(@PathVariable Long id,
			@RequestParam Double amount) {
    	return CardAccountsManager.INSTANCE.authorize(id, amount);
	}
	
    @RequestMapping(value = "/{id}/capture")
	public Transaction capture(@PathVariable Long id,
			@RequestParam Long transactionId, 
			@RequestParam Double amount) {
    	return CardAccountsManager.INSTANCE.capture(id, transactionId, amount);
	}
	
    @RequestMapping(value = "/{id}/refund")
	public Transaction refund (@PathVariable Long id,
			@RequestParam Long transactionId, 
			@RequestParam Double amount) {
    	return CardAccountsManager.INSTANCE.refund(id, transactionId, amount);
	}
	
    @RequestMapping(value = "/{id}/reverse")
	public Transaction reverse (@PathVariable Long id,
			@RequestParam Long transactionId, 
			@RequestParam Double amount) {
    	return CardAccountsManager.INSTANCE.reverse(id, transactionId, amount);
	}
    
    @RequestMapping(value = "/{id}/summary")
	public List<Transaction> summary (@PathVariable Long id) {
    	return CardAccountsManager.INSTANCE.getSummary(id);
	}
    
    @RequestMapping(value = "/{id}/pendingTransactions")
	public List<Transaction> getPendingTransactions (@PathVariable Long id) {
    	return CardAccountsManager.INSTANCE.getPendingTransactions(id);
	}
    
    @RequestMapping(value = "/{id}/balance")
	public CardAccountShortenedBalance balance (@PathVariable Long id) {
    	return CardAccountsManager.INSTANCE.getShortenedBalance(id);
	}
}
