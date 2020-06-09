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
 * jcc_rpc_java需升级到最新版本，见https://jitpack.io/#JCCDex/jcc_rpc_java/v2.8
 * 
 * 
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
		// 建议节点定期更新，防止因为节点变更导致不可用
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
					SignedTransaction tx = null;
					try {
					  tx = payment.sign(secret);
					} catch (Exception e) {
						// 签名异常
						// 根据实际情况做下一步操作
						return;
					}

					// 交易hash, 和最后上链的hash一致。
					Hash256 hash = tx.hash;
					String blob = tx.tx_blob;

					System.out.println("交易blob: " + tx.tx_blob);
					System.out.println("交易hash: " + hash.toHex());

					nodeRpc.transfer(blob, new JCallback() {
						@Override
						public void onResponse(String code, String response) {
							System.out.println(code);
							System.out.println(response);
							// code说明见http://developer.jingtum.com/error_code.html
							// 一般情况下code为tesSUCCESS， 就表示交易成功
							// 但是也存在在tesSUCCESS情况下，实际没有成功上链的情况，最好提交交易过后，根据hash做二次验证
							// 节点每10s出块，建议异步验证交易详情，时间间隔建议最少10s之后
							// 根据业务需求决定是否做二次验证
							nodeRpc.requestTx(hash.toHex(), new JCallback() {
								
								@Override
								public void onResponse(String code, String res) {
									if (code.equals("success")) {
										// transactionResult说明见http://developer.jingtum.com/error_code.html
										String transactionResult = JSONObject.parseObject(res).getJSONObject("result").getJSONObject("meta").getString("TransactionResult");
										if (transactionResult.equals("tesSUCCESS")) {
											// 上链验证成功
										} else {
											// 上链验证失败，根据业务需求做下一步操作
										}
									} else {
										// 上链验证失败，根据业务需求做下一步操作
									}
								}
								
								@Override
								public void onFail(Exception arg0) {
									// 异常状态，根据业务需求做下一步操作
								}
							});
						}

						@Override
						public void onFail(Exception e) {
							// 异常状态，根据业务需求做下一步操作
						}
					});

				} else {
					// 获取sequence失败
					// 根据业务需求做下一步操作
				}
			}

			@Override
			public void onFail(Exception e) {
				// 异常状态，根据业务需求做下一步操作

			}
		});
	}

}
