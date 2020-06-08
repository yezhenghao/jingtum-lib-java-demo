package demo;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.blink.jtblc.core.coretypes.AccountID;
import com.blink.jtblc.core.coretypes.Amount;
import com.blink.jtblc.core.coretypes.hash.Hash256;
import com.blink.jtblc.core.coretypes.uint.UInt32;
import com.blink.jtblc.core.types.known.tx.signed.SignedTransaction;
import com.blink.jtblc.core.types.known.tx.txns.Payment;
import com.jccdex.rpc.api.JccdexNodeRpc;
import com.jccdex.rpc.base.JCallback;
import com.jccdex.rpc.url.JccdexUrl;

/**
 * 通过rpc节点发起http请求，本地签名后提交转账交易 本地签名需要先获取sequence
 * 
 * 正式链的rpc节点可通过https://gateway.swtc.top/rpcservice获取
 * 
 * jcc_rpc_java需升级到最新版本，见https://jitpack.io/#JCCDex/jcc_rpc_java/v2.7
 * 
 * @author GinMu
 *
 */
public class PaymentWithNodeRpcDemo {

	public static void main(String[] args) throws Exception {

		String account = "";
		String secret = "";
		String to = "";
		String value = "0.01";
		String token = "CNY";
		String issuer = "jGa9J9TkqtBcUoHe2zqhVFFbgUVED6o9or";
		// token不为swt时，需要issuer和token名称
		Amount amount = new Amount(new BigDecimal(value), token.toUpperCase(), issuer);

		// token为swt时, 不需要issuer和token名称
		// Amount amount = new Amount(new BigDecimal(1));
		Payment payment = new Payment();
		payment.as(AccountID.Account, account);
		payment.as(AccountID.Destination, to);
		payment.as(Amount.Amount, amount);
		payment.as(Amount.Fee, "100");
		payment.flags(new UInt32(0));

		// 正式链的rpc节点可通过https://gateway.swtc.top/rpcservice获取
		JccdexUrl jccUrl = new JccdexUrl("39.104.188.146", false, 50333);
		JccdexNodeRpc nodeRpc = JccdexNodeRpc.getInstance();
		nodeRpc.setmBaseUrl(jccUrl);

		nodeRpc.requestSequence(account, new JCallback() {

			@Override
			public void onResponse(String code, String response) {
				System.out.println(code);
				System.out.println(response);
				// code为success表示获取sequence成功
				if (code.equals("success")) {
					int sequence = JSONObject.parseObject(response).getJSONObject("result")
							.getJSONObject("account_data").getIntValue("Sequence");
					payment.sequence(new UInt32(sequence));

					SignedTransaction tx = payment.sign(secret);

					// 交易hash, 和最后上链的hash一致。
					Hash256 hash = tx.hash;
					String blob = tx.tx_blob;

					System.out.println("交易blob: " + tx.tx_blob);
					System.out.println("交易hash: " + hash);

					nodeRpc.transfer(blob, new JCallback() {

						// code说明见http://developer.jingtum.com/error_code.html
						// 一般情况下code为tesSUCCESS， 就表示交易成功
						// 但是也存在在tesSUCCESS情况下，实际没有成功上链的情况，最好提交交易过后，根据hash做二次验证
						// 可根据浏览器API查询交易详情：https://github.com/JCCDex/JingChang-Document/blob/master/zh-CN/Jingchang-Explorer-Server.md#3-%E6%A0%B9%E6%8D%AE%E5%93%88%E5%B8%8C%E6%9F%A5%E8%AF%A2%E4%BA%A4%E6%98%93%E8%AF%A6%E7%BB%86
						@Override
						public void onResponse(String code, String response) {
							System.out.println(code);
							System.out.println(response);
						}

						@Override
						public void onFail(Exception e) {

						}
					});

				} else {
					// 获取sequence失败
				}
			}

			@Override
			public void onFail(Exception e) {
			}
		});
	}

}
