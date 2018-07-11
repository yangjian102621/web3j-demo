package com.blockfox.blockchain;

import com.blockfox.blockchain.contracts.Voting;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * 智能合约测试
 * @author yangjian
 * @since 18-7-11
 */
public class ContractTest {

	static Logger logger = LoggerFactory.getLogger(ContractTest.class);

	private Web3j web3j = Web3j.build(new HttpService("http://localhost:8545/"));

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
	 * 加载已经发布的合约
	 */
	@Test
	public void loadContract() throws Exception {

		String contractAddress = "0xadae74d6a9c1a2591f55f5f2dd0c2f291959f519";
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		ClientTransactionManager ctm = new ClientTransactionManager(web3j, accounts.get(0));
		Voting voting = Voting.load(contractAddress, web3j, ctm, Contract.GAS_PRICE, Contract.GAS_LIMIT);
		TransactionReceipt receipt = voting.voteFor(stringToByte32("Tommy")).send();
		logger.info("transaction Hash: "+receipt.getTransactionHash());
	}
}
