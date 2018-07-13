package com.blackfox.blockchain;

import com.blackfox.blockchain.service.AccountService;
import com.blackfox.blockchain.contracts.Voting;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 智能合约测试
 * @author yangjian
 * @since 18-7-11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContractTest {

	static Logger logger = LoggerFactory.getLogger(ContractTest.class);

	private Web3j web3j = Web3j.build(new HttpService("http://localhost:8545/"));

	@Autowired
	AccountService accountService;

	@Test
	public void getBalance() throws IOException {
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		BigDecimal balance = accountService.getBalance(accounts.get(0));
		BigDecimal balance_1 = accountService.getBalance(accounts.get(1));
		logger.info("balance 0: "+balance);
		logger.info("balance 1: "+balance_1);
	}

	/**
	 * 发布合约测试
	 */
	@Test
	public void deployContract() throws Exception {

		// 指定部署合约的账户
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		ClientTransactionManager ctm = new ClientTransactionManager(web3j, accounts.get(0));
		// 准备参数：候选人名单
		List<byte[]> candidates = new ArrayList<>();
		candidates.add(stringToByte32("Tommy"));
		candidates.add(stringToByte32("Jerry"));
		candidates.add(stringToByte32("Micky"));
		//部署合约
		Voting voting = Voting.deploy(
				web3j,
				ctm,
				Contract.GAS_PRICE,
				Contract.GAS_LIMIT,
				candidates).send();

		//获取合约的部署地址
		String contractAddress = voting.getContractAddress();
		logger.info("contract address: "+contractAddress);
	}

	/**
	 * 把字符创转为 32 位定长字节数组
	 * @param str
	 * @return
	 */
	private static byte[] stringToByte32(String str){
		byte[] a = new byte[32];
		System.arraycopy(str.getBytes(),0,a,32-str.length(),str.length());
		return a;
	}

	/**
	 * 加载已经发布的合约, 调用合约方法进行投票
	 */
	@Test
	public void voteFor() throws Exception {

		String contractAddress = "0x0549859accaa014078165c6ac9a2a4597e00da0b";
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		// 使用第一个账户进行投票
		ClientTransactionManager ctm1 = new ClientTransactionManager(web3j,accounts.get(0));
		Voting v1 = Voting.load(contractAddress,web3j,ctm1,Contract.GAS_PRICE,Contract.GAS_LIMIT);
		TransactionReceipt receipt1 = v1.voteFor(stringToByte32("Tommy")).send();
		// 使用第二个账户进行投票
		ClientTransactionManager ctm2 = new ClientTransactionManager(web3j,accounts.get(1));
		Voting v2 = Voting.load(contractAddress, web3j, ctm2, Contract.GAS_PRICE, Contract.GAS_LIMIT);
		TransactionReceipt receipt2 = v2.voteFor(stringToByte32("Jerry")).send();
		// 打印投票结果
		logger.info("transaction Hash 1: "+receipt1.getTransactionHash());
		logger.info("transaction Hash 2: "+receipt2.getTransactionHash());

	}

	/**
	 * 调用合约， 查看某人的投票数
	 */
	@Test
	public void getVoteFor() throws Exception {

		String contractAddress = "0x0549859accaa014078165c6ac9a2a4597e00da0b";
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		// 指定合约的调用账户
		ClientTransactionManager cmt = new ClientTransactionManager(web3j, accounts.get(0));
		Voting voting = Voting.load(contractAddress, web3j, cmt, Contract.GAS_PRICE, Contract.GAS_LIMIT);
		// 分别获取 Tommy 和 Jerry 的投票数
		BigInteger numTommy = voting.getVotesFor(stringToByte32("Tommy")).send();
		BigInteger numJerry = voting.getVotesFor(stringToByte32("Jerry")).send();

		//获取第一个候选人
		String name = new String(voting.candidates(new BigInteger("0")).send());

		logger.info("ticket number of Tommy: "+ numTommy);
		logger.info("ticket number of Jerry: "+ numJerry);
		logger.info(name.trim());
	}
}
