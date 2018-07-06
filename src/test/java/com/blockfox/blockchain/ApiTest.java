package com.blockfox.blockchain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author yangjian
 * @since 2018-07-06 下午5:27.
 */
public class ApiTest {

	static Logger logger = LoggerFactory.getLogger(ApiTest.class);

	private Web3j  web3j = Web3j.build(new HttpService("http://localhost:7545/"));

	@Test
	public void  getVersion() throws IOException {

		Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
		String clientVersion = web3ClientVersion.getWeb3ClientVersion();
		System.out.println(clientVersion);

	}

	@Test
	public void getBalance() throws IOException {

		Request<?, EthGetBalance> request = web3j.ethGetBalance("0x2eaBCA3C9Ee38a3896cBA2e2e74E613f80332194", DefaultBlockParameterName.LATEST);
		BigInteger balance = request.send().getBalance();
		BigDecimal balanceEther = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
		System.out.println(balanceEther);
	}

	@Test
	public void transfer() throws Exception {
		//付款人的私钥
		Credentials credentials = Credentials.create("2f128004e9510bde8d977a07549a5d83c822c86e0c7ce6030f01556f586fc37f");
		logger.info("addressFrom: "+ credentials.getAddress());
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
}
