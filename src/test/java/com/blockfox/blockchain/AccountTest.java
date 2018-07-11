package com.blockfox.blockchain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Account API 测试
 * @author yangjian
 * @since 2018-07-09 上午11:28.
 */
public class AccountTest {

	static Logger logger = LoggerFactory.getLogger(AccountTest.class);

	private Web3j web3j;

	/**
	 * 初始化 web3j
	 */
	private void initWeb3j() {
		web3j = Web3j.build(new HttpService("http://localhost:8545/"));
	}

	/**
	 * 创建钱包账号
	 * @throws Exception
	 */
	@Test
	public void  create() throws Exception {
		ECKeyPair keyPair = Keys.createEcKeyPair();
		String privateKey = keyPair.getPrivateKey().toString(16);
		String publicKey = keyPair.getPublicKey().toString(16);
		//通过秘钥对来获取地址
		String address = Keys.getAddress(keyPair);
		//通过公钥来获取地址
		String address1 = Keys.getAddress(publicKey);

		logger.info("private key: "+ privateKey);
		logger.info("public key: "+ publicKey);
		logger.info("address: 0x"+ address);
		logger.info("address: 0x"+ address1);
	}

	/**
	 * 通过私钥来恢复账户和钱包地址
	 */
	@Test
	public void getAddressByPrivateKey() {
		//私钥字符串
		String privateKeyStr = "133be114715e5fe528a1b8adf36792160601a2d63ab59d1fd454275b31328791";
		//转换成 BigInt
		BigInteger privateKey = new BigInteger(privateKeyStr, 16);
		ECKeyPair keyPair = ECKeyPair.create(privateKey);
		String address = Keys.getAddress(keyPair);

		logger.info("address: 0x"+ address);

	}

	/**
	 * 获取节点账户
	 */
	@Test
	public void getAccounts() throws IOException {
		initWeb3j();
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		accounts.forEach(accout -> {
			logger.info("address: "+accout);
		});

	}

	/**
	 * 获取账户余额
	 */
	@Test
	public void getBalance() throws IOException {

		initWeb3j();
		String address = web3j.ethAccounts().send().getAccounts().get(0);
		logger.info("balance1: "+ getBalance(address));
		logger.info("balance2: "+ getBalance("0xc810de81dfc703530407528b49f1a32ed34dd57e"));
	}

	private BigDecimal getBalance(String address) throws IOException {
		BigInteger balance = web3j.ethGetBalance(address,
				DefaultBlockParameterName.LATEST).send().getBalance();
		BigDecimal balanceEther = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
		return balanceEther;
	}

}
