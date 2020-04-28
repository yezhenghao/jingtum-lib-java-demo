package demo;

import com.blink.jtblc.client.Remote;
import com.blink.jtblc.client.bean.Account;
import com.blink.jtblc.connection.Connection;
import com.blink.jtblc.connection.ConnectionFactory;

public class RequestTxDemo {
	public static void main(String[] args) throws Exception {
		Connection conn = ConnectionFactory.getCollection("wss://hc.jingtum.com:5020/");
		Remote remote = new Remote(conn, true);
		String hash = "7D8554BE9EEA1AC7CA9D4DD437D27A5BEB0339A7E538907CA69EEF9674F8A172";
		Account bean = remote.requestTx(hash);
		System.out.println(bean.getAccount());
		Connection.close();
	}
}
