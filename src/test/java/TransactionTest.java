import com.blockfox.blockchain.ExchangeTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * 发送交易测试
 * 其实整个以太坊就是一个巨大的分布式状态机，比如说最著名的状态机就是账户余额，而改变状态是通过发送交易来实现。
 * 以太坊中的交易分为两种，一种是普通交易，一种是裸交易。
 * 普通交易： 普通交易由节点负责进行签名
 * 裸交易： 由第三方应用自己签名，比如后端 Java 程序用钱包的私钥签好名，然后再发送到节点，由节点广播到网络
 * @author yangjian
 * @since 2018-07-10 上午10:16.
 */
public class TransactionTest {

	static Logger logger = LoggerFactory.getLogger(ExchangeTest.class);

	private Web3j web3j = Web3j.build(new HttpService("http://localhost:7545/"));

	/**
	 * 发送普通交易, 从第 0 个钱包转账到第 1 个钱包
	 */
	@Test
	public void sendTransaction() throws IOException {

		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		// 指定交易的发起方
		String from = accounts.get(0);
		String to = accounts.get(1);
		BigInteger gasPrice = null;
		BigInteger gasLimit = null;
		BigInteger value = Convert.toWei("10.23",Convert.Unit.ETHER).toBigInteger();
		BigInteger nonce = null;
		String data = null;
		Transaction tx = new Transaction(from,nonce,gasPrice,gasLimit,to,value,data);
		String txHash = web3j.ethSendTransaction(tx).send().getTransactionHash();

		logger.info("transaction hash: "+ txHash);
	}


}
