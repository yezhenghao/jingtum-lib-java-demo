package consortium;

import com.blink.jtblc.client.Wallet;
import com.blink.jtblc.config.Config;

/**
 * 井通联盟链操作 以商链为例
 * 
 * @author GinMu
 *
 */
public class WalletDemo {

	public static void main(String[] args) throws Exception {

		// 设置alphabet
		Config.setAlphabet("bpshnaf39wBUDNEGHJKLM4PQRST7VWXYZ2jcdeCg65rkm8oFqi1tuvAxyz");

		// 生成商链钱包
		Wallet wallet = Wallet.generate();
		String address = wallet.getAddress();
		String secret = wallet.getSecret();
		System.out.println("address: " + address);
		System.out.println("secret: " + secret);
		// 验证钱包地址
		Boolean isValidAddress = Wallet.isValidAddress(address);
		// 验证钱包密钥
		Boolean isValidSecret = Wallet.isValidSecret(secret);
		System.out.println("address is valid: " + isValidAddress);
		System.out.println("secret is valid: " + isValidSecret);
		// 根据钱包密钥获取钱包
		Wallet wallet2 = Wallet.fromSecret(secret);
		String address2 = wallet2.getAddress();
		System.out.println("address2 is equal to address: " + address2.equals(address));
	}
}
