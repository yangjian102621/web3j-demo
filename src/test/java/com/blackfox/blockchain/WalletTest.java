package com.blackfox.blockchain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Wallet API 测试
 * 在web3j中，钱包这个概念对应的是加密的私钥文件。因此，在一个钱包中，
 * 只能有一个私钥 —— 如果你需要在web3j中管理多个账户，就需要创建多个钱包
 * @author yangjian
 * @since 2018-07-09 上午11:43.
 */
public class WalletTest {

	static Logger logger = LoggerFactory.getLogger(WalletTest.class);

	/**
	 * 创建一个钱包
	 */
	@Test
	public void generate() throws Exception {
		//钱包密码
		String walletPass = "123456";
		//钱包路径
		File walletPath = new File("./keystore");
		if (!walletPath.exists()) {
			walletPath.mkdir();
		}
		//返回钱包文件名
		String walletName = WalletUtils.generateNewWalletFile(walletPass, walletPath, true);

		logger.info("Wallet file name: "+walletName);
	}

	/**
	 * 通过私钥生成 keystore json file
	 */
	@Test
	public void generateWalletFileByPrivateKey() throws CipherException, IOException {

		//私钥字符串
		String privateKeyStr = "133be114715e5fe528a1b8adf36792160601a2d63ab59d1fd454275b31328791";
		//转换成 BigInt
		BigInteger privateKey = new BigInteger(privateKeyStr, 16);
		ECKeyPair keyPair = ECKeyPair.create(privateKey);
		//钱包密码
		String walletPass = "123456";
		//钱包路径
		File walletPath = new File("./keystore");
		if (!walletPath.exists()) {
			walletPath.mkdir();
		}
		String walletName = WalletUtils.generateWalletFile(walletPass, keyPair, walletPath, false);

		logger.info("wallet name: "+ walletName);
	}

	/**
	 * 生成带助记词的钱包
	 * @throws Exception
	 */
	@Test
	public void newBip39Wallet() throws Exception {

		String password = "123456";
		File dest = new File("./keystore");
		Bip39Wallet wallet = WalletUtils.generateBip39Wallet(password,dest);
		logger.info(wallet.getMnemonic());
		logger.info(wallet.getFilename());
	}
}
