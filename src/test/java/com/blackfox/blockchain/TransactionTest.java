package com.blackfox.blockchain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

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

	private Web3j web3j = Web3j.build(new HttpService("http://localhost:8545/"));
	// 交易确认超时时间，单位是毫秒
	static final Long RECEIPT_TIMEOUT = 300000L;
	// 交易确认查询时间间隔
	static final Integer RECEIPT_INTERVAL = 1000;

	/**
	 * 发送普通交易, 从第 0 个钱包转账到第 1 个钱包
	 */
	@Test
	public void sendTransaction() throws Exception {

		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		// 指定交易的发起方
		String from = accounts.get(0);
		String to = "0xc810de81dfc703530407528b49f1a32ed34dd57e";
		// 设置 gas 价格，如果设置为默认值则为 null
		BigInteger gasPrice = Convert.toWei("20",Convert.Unit.GWEI).toBigInteger();
		// 设置 gas limit
		BigInteger gasLimit = BigInteger.valueOf(30000L);
		BigInteger value = Convert.toWei("0",Convert.Unit.ETHER).toBigInteger();
		BigInteger nonce = null;
		String data = "fuck";
		Transaction tx = new Transaction(from,nonce,gasPrice,gasLimit,to,value,data);
		String txHash = web3j.ethSendTransaction(tx).send().getTransactionHash();
		logger.info("transaction hash: "+ txHash);
		//等待交易返回结果（收据）
		TransactionReceipt transactionReceipt = waitForTransactionReceipt(txHash);
		logger.info("Gas used:"+ transactionReceipt.getGasUsed());
	}

	/**
	 * 发送裸交易
	 */
	@Test
	public void sendRawTransaction() throws Exception {
		// 加载钱包, 作为付款账户
		String pass = "123456";
		Credentials credentials = WalletUtils.loadCredentials(pass,
				"./keystore/UTC--2018-07-09T09-54-26.344000000Z--c810de81dfc703530407528b49f1a32ed34dd57e.json");

		logger.info("sender: "+ credentials.getAddress());
		// 收款账户
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		String to = accounts.get(1);
		// 设置 Gas 值
		BigInteger gasPrice = BigInteger.valueOf(20L);
		BigInteger gasLimit = BigInteger.valueOf(210000L);
		BigInteger value = Convert.toWei("1",Convert.Unit.ETHER).toBigInteger();
		//附加数据
		String data = "Hello world";
		String hexData = Numeric.toHexString(data.getBytes());
		BigInteger nonce = getNonce(credentials.getAddress());
		// 创建裸交易对象
		RawTransaction rawTx = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,to,value,hexData);
		// 私用发送交易账户的凭证对交易进行签名
		byte[] signedMessage = TransactionEncoder.signMessage(rawTx,credentials);
		String signedTransactionData = Numeric.toHexString(signedMessage);
		// 发送裸交易请求
		String txHash = web3j.ethSendRawTransaction(signedTransactionData).send().getTransactionHash();
		logger.info("transaction hash: "+ txHash);
		// 等待交易返回结果（收据）
		logger.info("P{}", waitForTransactionReceipt(txHash));

	}

	/**
	 * 发送受控交易，把提交交易、等待收据这两个环节封装起来。
	 * Transfer类实现了受控转账交易，其sendFunds()方法返回一个RemoteCall对象 该返回对象的send()方法将真正触发以太坊交易的发送。
	 * 对交易收据的等待是在交易管理器里完成的。在交易管理器中将在发送交易后启动一个 线程等待并最终返回交易收据。
	 * 默认情况下，web3j交易管理器将以15秒的周期最多检查40次交易收据。15秒是以太坊默认的 出块时间。
	 */
	@Test
	public void transactionManager() throws Exception {

		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		String from = accounts.get(0);
		String to = accounts.get(1);
		BigDecimal value = BigDecimal.valueOf(5.5);
		// 获取收据的时候轮询的次数
		int attemptTimes = 10;
		// 每次轮询时间间隔（单位：毫秒）
		int sleepDuration = 1000;
		ClientTransactionManager ctm = new ClientTransactionManager(web3j,from,attemptTimes,sleepDuration);
		Transfer transfer = new Transfer(web3j,ctm);
		TransactionReceipt receipt = transfer.sendFunds(to,value,Convert.Unit.ETHER).send();

		logger.info("Transaction complete:");
		logger.info("trans hash=" + receipt.getTransactionHash());
		logger.info("from :" + receipt.getFrom());
		logger.info("to:" + receipt.getTo());
		logger.info("gas used=" + receipt.getGasUsed());
		logger.info("status: " + receipt.getStatus());
	}

	/**
	 * 裸交易管理器测试
	 */
	@Test
	public void rawTransactionManager() throws Exception {

		// 加载钱包, 作为付款账户
		String pass = "123456";
		Credentials credentials = WalletUtils.loadCredentials(pass,
				"./keystore/UTC--2018-07-09T09-54-26.344000000Z--c810de81dfc703530407528b49f1a32ed34dd57e.json");

		logger.info("sender: "+ credentials.getAddress());
		// 收款账户
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		String to = accounts.get(0);

		BigDecimal value = BigDecimal.valueOf(5.5);
		// 获取收据的时候轮询的次数
		int attemptTimes = 10;
		// 每次轮询时间间隔（单位：毫秒）
		int sleepDuration = 1000;
		// 创建裸交易管理器
		RawTransactionManager rtm = new RawTransactionManager(web3j,credentials, attemptTimes, sleepDuration);
		Transfer transfer = new Transfer(web3j,rtm);
		// 发送裸交易请求
		TransactionReceipt receipt = transfer.sendFunds(to,value,Convert.Unit.ETHER).send();

		logger.info("Transaction complete:");
		logger.info("trans hash=" + receipt.getTransactionHash());
		logger.info("from :" + receipt.getFrom());
		logger.info("to:" + receipt.getTo());
		logger.info("gas used=" + receipt.getGasUsed());
		logger.info("status: " + receipt.getStatus());
	}

	/**
	 * 生成 nonce 对抗重放攻击
	 * @param account
	 * @return
	 */
	private BigInteger getNonce(String account) throws IOException {
		return web3j.ethGetTransactionCount(account, DefaultBlockParameterName.LATEST)
				.send().getTransactionCount();
	}

	/**
	 * 等待交易结果返回（收据）， 建议在生产环境中使用队列解决，维护一个队列，每次从队列里拿出一条 txHash 启动一个线程去查询。
	 * @param txHash
	 * @return
	 * @throws Exception
	 */
	private TransactionReceipt waitForTransactionReceipt(String txHash) throws Exception {

		logger.info("wait for receipt...");

		long t0 = System.currentTimeMillis();
		Optional<TransactionReceipt> receipt;
		while(true) {
			receipt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();
			if(receipt.isPresent()) {
				logger.info("got receipt successfully :"+ txHash);
				return receipt.get();
			}
			long t1 = System.currentTimeMillis();
			if((t1-t0) > RECEIPT_TIMEOUT) {
				logger.error("confirm transaction delay:" + txHash);
				return null;
			}
			Thread.sleep(RECEIPT_INTERVAL);
		}
	}

	/**
	 * 根据交易 hash 获取交易详情
	 * @throws IOException
	 */
	@Test
	public void getTransactionByHash() throws IOException {
		String txHash = "0x0c89207dc160180da17fee0b6d4cdb817a07e1ee8ecdb719489343e536751fe2";
		Optional<org.web3j.protocol.core.methods.response.Transaction> transaction = web3j.ethGetTransactionByHash(txHash).send().getTransaction();

		if (transaction.isPresent()) {
			String inputData = new String(Numeric.hexStringToByteArray(transaction.get().getInput()));
			logger.info("from: "+ transaction.get().getFrom());
			logger.info("to: "+ transaction.get().getTo());
			logger.info("gas limit: "+ transaction.get().getGas());
			logger.info("gas price: "+ transaction.get().getGasPrice());
			logger.info("amount: "+ transaction.get().getValue());
			logger.info("blockHash: "+ transaction.get().getBlockHash());
			logger.info("input data: "+inputData);

			//获取交易时间戳
			String blockHash = transaction.get().getBlockHash();
			EthBlock.Block block = web3j.ethGetBlockByHash(blockHash, true).send().getBlock();
			logger.info("block gas used: "+ block.getGasUsed());
			logger.info("block gas limit: "+ block.getGasLimit());
			logger.info("block timestamp: "+ block.getTimestamp());
		}
	}

}
