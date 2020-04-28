package demo;

import java.math.BigDecimal;

import com.blink.jtblc.core.coretypes.AccountID;
import com.blink.jtblc.core.coretypes.Amount;
import com.blink.jtblc.core.coretypes.uint.UInt32;
import com.blink.jtblc.core.types.known.tx.signed.SignedTransaction;
import com.blink.jtblc.core.types.known.tx.txns.Payment;

public class SignDemo {

	private static String account = "";
	private static String secret = "";
	private static String to = "";

	public static void main(String[] args) throws Exception {
		String value = "0.01";
		String token = "CNY";
		String issuer = "jGa9J9TkqtBcUoHe2zqhVFFbgUVED6o9or";
		Amount amount = new Amount(new BigDecimal(value), token, issuer);
		Payment payment = new Payment();
		payment.as(AccountID.Account, account);
		payment.as(AccountID.Destination, to);
		payment.as(Amount.Amount, amount);
		payment.as(Amount.Fee, "0.00001");
		payment.sequence(new UInt32(5914));
		payment.flags(new UInt32(0));
		SignedTransaction tx = payment.sign(secret);
		System.out.println("transaction payload: " + tx.tx_blob);
		System.out.println("transaction hash: " + tx.hash);
	}

}
