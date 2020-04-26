package demo;

import java.util.ArrayList;

import com.blink.jtblc.client.Remote;
import com.blink.jtblc.client.Transaction;
import com.blink.jtblc.client.bean.AmountInfo;
import com.blink.jtblc.client.bean.TransactionInfo;
import com.blink.jtblc.connection.Connection;
import com.blink.jtblc.connection.ConnectionFactory;

public class PaymentDemo {

	// 钱包地址
	private static String account = "";
//	// 钱包秘钥
	private static String secret = "";
//	// 收款地址
	private static String to = "";

	public static void main(String[] args) throws Exception {

		Connection conn = ConnectionFactory.getCollection("wss://hc.jingtum.com:5020/");
		// true表示经过本地签名后提交交易
		Remote remote = new Remote(conn, true);
		AmountInfo amount = new AmountInfo();
		// 设置转账数量
		amount.setValue("1");
		// 设置token名称
		amount.setCurrency("SWT");
		// 设置发行方钱包地址
		// 对于swt为""
		amount.setIssuer("");
		// 对于swt为true, 其他为false
		amount.setIsNative(true);
		// 构建转账交易
		Transaction tx = remote.buildPaymentTx(account, to, amount);
		ArrayList<String> memo = new ArrayList<>();
		// 添加备注信息
		memo.add("test");
		// 添加备注
		tx.addMemo(memo);
		// 设置密钥
		tx.setSecret(secret);
		TransactionInfo bean = tx.submit();
		System.out.println("转账结果: " + bean.getEngineResult());
		System.out.println("转账hash: " + bean.getTxJson().getHash());
		Connection.close();
	}
}