package prepaidCard.prepaidCard;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.imaginecurve.prepaidCard.implementation.CardAccountShortenedBalance;
import com.imaginecurve.prepaidCard.implementation.CardAccountsManager;
import com.imaginecurve.prepaidCard.implementation.Transaction;
import com.imaginecurve.prepaidCard.implementation.TransactionStatus;

public class AccountCardManagerTest {
	
	@Test
	public void testCreate() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		Assert.assertNotNull(cardId);
	}
	
	@Test
	public void testTopUp() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 2.0);
		Transaction t = CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		Double expected = new Double(9.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		Assert.assertEquals(expected, t.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}
	
	@Test
	public void testAuthorize() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 4.0);
		Double expected = new Double(8.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		
		Assert.assertEquals(TransactionStatus.PENDING, auth.getStatus());
		Assert.assertEquals(expected, auth.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}
	
	@Test
	public void testAuthorizeInsufficientFunds() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 24.0);
		Double expected = new Double(12.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		
		Assert.assertEquals(TransactionStatus.INSUFFICIENT_FUNDS, auth.getStatus());
		Assert.assertEquals(expected, auth.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}

	@Test
	public void testCapture() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 4.0);
		Double expected = new Double(8.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		Transaction capture = CardAccountsManager.INSTANCE.capture(cardId,
				auth.getId(), 4.0);
		Assert.assertEquals(TransactionStatus.CONFIRMED, capture.getStatus());
		Assert.assertEquals(expected, auth.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}
	
	@Test
	public void testPartialCapture() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 4.0);
		Double expected = new Double(8.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		Transaction capture = CardAccountsManager.INSTANCE.capture(cardId,
				auth.getId(), 3.0);
		Assert.assertEquals(TransactionStatus.CONFIRMED, capture.getStatus());
		Assert.assertEquals(TransactionStatus.PENDING, auth.getStatus());
		Transaction capture2 = CardAccountsManager.INSTANCE.capture(cardId,
				auth.getId(), 1.0);
		Assert.assertEquals(TransactionStatus.CONFIRMED, capture2.getStatus());
		
		List<Transaction> pendingTransactions = 
				CardAccountsManager.INSTANCE.getPendingTransactions(cardId);
		Assert.assertEquals(0,pendingTransactions.size());
		Assert.assertEquals(expected, auth.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}
	
	@Test
	public void testPartialCaptureInsufficientFunds() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 4.0);
		Double expected = new Double(8.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		Transaction capture = CardAccountsManager.INSTANCE.capture(cardId,
				auth.getId(), 3.0);
		Assert.assertEquals(TransactionStatus.CONFIRMED, capture.getStatus());
		Assert.assertEquals(TransactionStatus.PENDING, auth.getStatus());
		Transaction capture2 = CardAccountsManager.INSTANCE.capture(cardId,
				auth.getId(), 3.0);
		Assert.assertEquals(TransactionStatus.INSUFFICIENT_AUTHORIZATION, capture2.getStatus());
		
		List<Transaction> pendingTransactions = 
				CardAccountsManager.INSTANCE.getPendingTransactions(cardId);
		Assert.assertEquals(1,pendingTransactions.size());
		Assert.assertEquals(expected, auth.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}

	@Test
	public void testReverse() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 4.0);
		
		Transaction refund = CardAccountsManager.INSTANCE.reverse(cardId,
				auth.getId(), 4.0);
		Double expected = new Double(12.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		List<Transaction> pendingTransactions = 
				CardAccountsManager.INSTANCE.getPendingTransactions(cardId);
		
		Assert.assertEquals(0,pendingTransactions.size());
		Assert.assertEquals(TransactionStatus.CANCELLED, refund.getStatus());
		Assert.assertEquals(expected, auth.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}
	
	@Test
	public void testReverseInsufficientAuthorization() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 4.0);
		
		Transaction reverse1 = CardAccountsManager.INSTANCE.reverse(cardId,
				auth.getId(), 3.0);
		Transaction reverse2 = CardAccountsManager.INSTANCE.reverse(cardId,
				auth.getId(), 3.0);
		Double expected = new Double(11.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		List<Transaction> pendingTransactions = 
				CardAccountsManager.INSTANCE.getPendingTransactions(cardId);
		
		Assert.assertEquals(1,pendingTransactions.size());
		Assert.assertEquals(TransactionStatus.PENDING, reverse1.getStatus());
		Assert.assertEquals(TransactionStatus.INSUFFICIENT_FUNDS_FOR_REVERSE,
				reverse2.getStatus());
		Assert.assertEquals(expected, auth.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}

	@Test
	public void testRefund() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 4.0);
		Transaction capture = CardAccountsManager.INSTANCE.capture(cardId,
				auth.getId(), 4.0);
		Transaction refund = CardAccountsManager.INSTANCE.refund(cardId,
				capture.getId(), 4.0);
		
		Double expected = new Double(12.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		List<Transaction> pendingTransactions = 
				CardAccountsManager.INSTANCE.getPendingTransactions(cardId);
		
		Assert.assertEquals(0,pendingTransactions.size());
		Assert.assertEquals(TransactionStatus.CONFIRMED, refund.getStatus());
		Assert.assertEquals(expected, refund.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}
	
	@Test
	public void testRefundInsufficientFundsForRefund() {
		Long cardId = CardAccountsManager.INSTANCE.createCard();
		CardAccountsManager.INSTANCE.topUp(cardId, 5.0);
		CardAccountsManager.INSTANCE.topUp(cardId, 7.1);
		
		Transaction auth = CardAccountsManager.INSTANCE.authorize(cardId, 4.0);
		Transaction capture = CardAccountsManager.INSTANCE.capture(cardId,
				auth.getId(), 4.0);
		Transaction refund1 = CardAccountsManager.INSTANCE.refund(cardId,
				capture.getId(), 2.0);
		Transaction refund2 = CardAccountsManager.INSTANCE.refund(cardId,
				capture.getId(), 3.0);
		
		Double expected = new Double(10.1);
		CardAccountShortenedBalance balance = 
				CardAccountsManager.INSTANCE.getShortenedBalance(cardId);
		List<Transaction> pendingTransactions = 
				CardAccountsManager.INSTANCE.getPendingTransactions(cardId);
		
		Assert.assertEquals(0,pendingTransactions.size());
		Assert.assertEquals(TransactionStatus.INSUFFICIENT_FUNDS_FOR_REFUND, refund2.getStatus());
		Assert.assertEquals(expected, refund1.getAccountBalanceAfterTransaction());
		Assert.assertEquals(expected, balance.getCurrentAmount());
	}

}
