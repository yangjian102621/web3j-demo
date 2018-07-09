package com.blockfox.blockchain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;

import java.io.IOException;

/**
 * 账户凭证测试
 * 钱包文件中保存的是加密后的私钥，显然，只要持有生成钱包文件时的密码， 就可以恢复私钥，进而重新获得公钥和账户地址，
 * 凭证Credentials非常重要，因为在我们向以太坊提交交易时，总需要使用私钥 进行签名，而在Web3j中，通常使用凭证对象传入执行签名的方法。
 * @author yangjian
 * @since 2018-07-09 上午11:56.
 */
public class CredentialsTest {

	static Logger logger = LoggerFactory.getLogger(CredentialsTest.class);

	/**
	 * 通过钱包文件来加载凭证
	 */
	@Test
	public void loadCredentialsByWalletFile() throws IOException, CipherException {

		String pass = "123456";
		Credentials credentials = WalletUtils.loadCredentials(pass,
				"./keystore/UTC--2018-07-09T03-54-46.698000000Z--d912aecb07e9f4e1ea8e6b4779e7fb6aa1c3e4d8.json");
		ECKeyPair keyPair = credentials.getEcKeyPair();
		String address = credentials.getAddress();

		logger.info("key pairs: {}", keyPair.getPrivateKey());
		logger.info("address: "+ address);
	}

	/**
	 * 通过私钥来加载凭证
	 */
	@Test
	public void loadCredentialsByPrivateKey() {

		Credentials credentials = Credentials.create("2f128004e9510bde8d977a07549a5d83c822c86e0c7ce6030f01556f586fc37f");
		logger.info("wallet address: "+ credentials.getAddress());
	}
}
