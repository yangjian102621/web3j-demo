package com.blockfox.blockchain;

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

	private Web3j  web3j = Web3j.build(new HttpService("http://localhost:7545/"));

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
		String walletFilePath = "/home/yangjian/";
		walletFileName = WalletUtils.generateNewWalletFile("123456", new File(walletFilePath), false);
		logger.info("wallet name: "+walletFileName);
	}

	/**
	 * 获取钱包地址余额
	 * @throws IOException
	 */
	@Test
	public void getBalance() throws IOException {

		Request<?, EthGetBalance> request = web3j.ethGetBalance("0x2eaBCA3C9Ee38a3896cBA2e2e74E613f80332194", DefaultBlockParameterName.LATEST);
		BigInteger balance = request.send().getBalance();
		//转换单位
		BigDecimal balanceEther = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
		System.out.println(balanceEther);
	}

	/**
	 * 通过私钥获取证书
	 * @return
	 */
	private Credentials getCredentialsByPrivateKey() {

		Credentials credentials = Credentials.create("2f128004e9510bde8d977a07549a5d83c822c86e0c7ce6030f01556f586fc37f");
		logger.info("wallet address: "+ credentials.getAddress());
		return  credentials;
	}

	/**
	 * 通过加载钱包的方式获取转账证书
	 * @return
	 */
	private Credentials loadWallet() throws IOException, CipherException {

		String walletFilePath="/home/yangjian/account";
		String passWord="111111";
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
		String addressTo = "0x1E3E2A1BeF13bE315559e3B1a2ed8aa39EA684e1";
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
