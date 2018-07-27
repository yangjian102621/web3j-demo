package com.blackfox.blockchain.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Using Infura with web3j， 使用 Infura 节点的 RPC 接口，访问以太坊测试网络和主网 API
 * @author yangjian
 * @since 2018-07-06 下午5:27.
 */
public class InfuraTest {

	static Logger logger = LoggerFactory.getLogger(InfuraTest.class);

	private Web3j  web3j = Web3j.build(new HttpService("https://ropsten.infura.io/CmcevUJgt4CvsYQLXqxv"));

	/**
	 * 获取客户端版本
	 * @throws IOException
	 */
	@Test
	public void  getVersion() throws IOException {

		Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
		String clientVersion = web3ClientVersion.getWeb3ClientVersion();
		System.out.println(clientVersion);

	}

	/**
	 * 创建钱包
	 */
	@Test
	public void createWallet() throws Exception {

		//钱包文件名称
		String walletFileName;
		//钱包地址
		String walletFilePath = "./keystore/";
		walletFileName = WalletUtils.generateNewWalletFile("123456", new File(walletFilePath), false);
		logger.info("wallet name: "+walletFileName);
	}

	/**
	 * 获取钱包地址余额
	 * @throws IOException
	 */
	@Test
	public void getBalance() throws IOException {

		Request<?, EthGetBalance> request = web3j.ethGetBalance("0xa7f875234682CAe685ce38f64380eEd2477b89D1", DefaultBlockParameterName.LATEST);
		BigInteger balance = request.send().getBalance();
		//转换单位
		BigDecimal balanceEther = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
		logger.info("balance: "+balanceEther);
	}

	/**
	 * 发送裸交易
	 */
	@Test
	public void sendRawTransaction() throws Exception {
		// 加载钱包, 作为付款账户
		Credentials credentials = getCredentialsByPrivateKey();

		logger.info("sender: "+ credentials.getAddress());
		String to = "0xfab9Eb2bFEb9969fA91e3095880132807FC10bB7";
		// 设置 Gas 值
		BigInteger gasPrice = BigInteger.valueOf(20L);
		BigInteger gasLimit = BigInteger.valueOf(210000L);
		BigInteger value = BigInteger.ONE;
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
	 * 通过私钥获取证书
	 * @return
	 */
	private Credentials getCredentialsByPrivateKey() {

		Credentials credentials = Credentials.create("a43b620bebadd804e7272def7791ae7856c23e14c50be25947a1b65dc292b598");
		logger.info("wallet address: "+ credentials.getAddress());
		return  credentials;
	}

	/**
	 * 通过加载钱包的方式获取转账证书
	 * @return
	 */
	private Credentials loadWallet() throws IOException, CipherException {

		String walletFilePath="./keystore/UTC--2018-07-09T03-49-26.401000000Z--591b1b54c060980b6592cf858b5f31e29365d38c.json";
		String passWord="123456";
		Credentials credentials = WalletUtils.loadCredentials(passWord, walletFilePath);
		logger.info("wallet address: "+ credentials.getAddress());
		return credentials;

	}

	@Test
	public void testLoadWallet() throws IOException, CipherException {
		loadWallet();
	}

	/**
	 * 测试通过私钥转帐
	 * @throws Exception
	 */
	@Test
	public void transferByPrivateKey() throws Exception {
		//付款人的私钥
		Credentials credentials = getCredentialsByPrivateKey();
		//收款人地址
		String addressTo = "0x981C0f6b3A31B00df8b83052Dc8C065209D60Aee";
		BigDecimal value = BigDecimal.valueOf(0.1);
		TransactionReceipt send = Transfer.sendFunds(web3j, credentials, addressTo, value, Convert.Unit.ETHER)
				.send();

		logger.info("Transaction complete:");
		logger.info("trans hash=" + send.getTransactionHash());
		logger.info("from :" + send.getFrom());
		logger.info("to:" + send.getTo());
		logger.info("gas used=" + send.getGasUsed());
		logger.info("status: " + send.getStatus());
	}

	/**
	 * 通过钱包文件来转账
	 */
	@Test
	public void transferByWalletFile() throws Exception {

		//付款人的私钥
		Credentials credentials = loadWallet();
		//收款人地址
		String addressTo = "0x1E3E2A1BeF13bE315559e3B1a2ed8aa39EA684e1";
		BigDecimal value = BigDecimal.valueOf(23.15);
		TransactionReceipt send = Transfer.sendFunds(web3j, credentials, addressTo, value, Convert.Unit.ETHER)
				.send();

		logger.info("Transaction complete:");
		logger.info("trans hash=" + send.getTransactionHash());
		logger.info("from :" + send.getFrom());
		logger.info("to:" + send.getTo());
		logger.info("gas used=" + send.getGasUsed());
		logger.info("status: " + send.getStatus());
	}
}
