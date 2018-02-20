package com.imaginecurve.prepaidCard.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imaginecurve.prepaidCard.implementation.CardAccountsManager;
import com.imaginecurve.prepaidCard.implementation.Transaction;


@RestController
@RequestMapping("/card")
@EnableAutoConfiguration
public class Application {
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
    
    @RequestMapping(value = "/create")
	public Long createCard() {
		return CardAccountsManager.INSTANCE.createCard();
	}
    
    @RequestMapping(value = "/{id}/topUp")
	public Transaction topUp (@PathVariable Long id, Double amount) {
    	return CardAccountsManager.INSTANCE.topUp(id, amount);
	}
	
    @RequestMapping(value = "/{id}/authorize")
	public Transaction authorize(@PathVariable Long id, Double amount) {
    	return CardAccountsManager.INSTANCE.authorize(id, amount);
	}
	
    @RequestMapping(value = "/{id}/capture")
	public Transaction capture(@PathVariable Long id, Long transactionId, Double amount) {
    	return CardAccountsManager.INSTANCE.capture(id, transactionId, amount);
	}
	
    @RequestMapping(value = "/{id}/refund")
	public Transaction refund (@PathVariable Long id, Long transactionId, Double amount) {
    	return CardAccountsManager.INSTANCE.refund(id, transactionId, amount);
	}
	
    @RequestMapping(value = "/{id}/reverse")
	public Transaction reverse (@PathVariable Long id, Long transactionId, Double amount) {
    	return CardAccountsManager.INSTANCE.reverse(id, transactionId, amount);
	}
}
