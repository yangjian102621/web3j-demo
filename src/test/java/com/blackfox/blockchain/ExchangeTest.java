package com.blackfox.blockchain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 基于 web3j 实现一个简单的交易所，钱包账号创建，查询余额，转账
 * @author yangjian
 * @since 2018-07-06 下午5:27.
 */
public class ExchangeTest {

	static Logger logger = LoggerFactory.getLogger(ExchangeTest.class);

	private Web3j  web3j = Web3j.build(new HttpService("http://localhost:8545/"));

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

		Request<?, EthGetBalance> request = web3j.ethGetBalance("0xc810de81dfc703530407528b49f1a32ed34dd57e", DefaultBlockParameterName.LATEST);
		BigInteger balance = request.send().getBalance();
		//转换单位
		BigDecimal balanceEther = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
		logger.info("balance: "+balanceEther);
	}

	/**
	 * 通过私钥获取证书
	 * @return
	 */
	private Credentials getCredentialsByPrivateKey() {

		Credentials credentials = Credentials.create("4a5f9ce564aaa77d94c9c57f4bd718b0697ace9fd3cf726c583af9403caef9b1");
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
		String addressTo = "0xc810de81dfc703530407528b49f1a32ed34dd57e";
		BigDecimal value = BigDecimal.valueOf(30.1415926);
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
