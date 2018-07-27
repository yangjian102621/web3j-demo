package com.blackfox.blockchain.controller;

import com.blackfox.blockchain.contracts.Voting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;
import org.web3j.utils.Async;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * 事件监听测试
 * @author yangjian
 * @since 2018-07-27 下午2:41.
 */
@RestController
@RequestMapping("/")
public class EventController {

	Logger logger = LoggerFactory.getLogger(getClass());

	// 设置事件轮询周期为 1 秒，通常默认是以太坊的出块时间 15 秒
	private Web3j web3j = Web3j.build(new HttpService("http://localhost:8545/"), 1000, Async.defaultExecutorService());

	/**
	 * 监听新的区块产生
	 */
	//@PostConstruct
	public void newBlock() {

		web3j.blockObservable(true).subscribe(ethBlock -> {
			EthBlock.Block block = ethBlock.getBlock();
			logger.info("hash: " + block.getHash());
			logger.info("number: " + block.getNumber());
			List<EthBlock.TransactionResult> transactions = block.getTransactions();
			transactions.forEach(tx -> {
				Transaction o = (Transaction) tx.get();
				logger.info("Hash: " + o.getHash());
				logger.info("From: "+o.getFrom());
				logger.info("To: "+o.getTo());
				logger.info("Value: "+o.getValue());
			});
		});

	}

	/**
	 * 监听新的交易事件
	 */
	//@PostConstruct
	public void newTransaction() {

		web3j.transactionObservable().subscribe(tx -> {
			logger.info("Hash: " + tx.getHash());
			logger.info("From: " + tx.getFrom());
			logger.info("To: " + tx.getTo());
			logger.info("Value: " + tx.getValue());
		});
	}

	/**
	 * 监听待处理的新交易
	 */
	//@PostConstruct
	public void appendingTransaction() {

		web3j.pendingTransactionObservable().subscribe(tx -> {
			logger.info("Hash: " + tx.getHash());
			logger.info("From: " + tx.getFrom());
			logger.info("To: " + tx.getTo());
			logger.info("Value: " + tx.getValue());
		});

	}

	/**
	 * 监听智能合约事件
	 */
	@PostConstruct
	public void EvmEvent() throws IOException {
		String contractAddress = "0x55a4ff652c36d2f9aaa386e9c462ded0cb493745";
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		ClientTransactionManager ctm = new ClientTransactionManager(web3j,accounts.get(0));
		Voting voting = Voting.load(contractAddress, web3j, ctm, Contract.GAS_PRICE, Contract.GAS_LIMIT);
		EthFilter filter = new EthFilter();
		voting.voteEventObservable(filter).subscribe(event -> {
			logger.info(event.voter + " : " + new String(event.candidate));
		});
	}

}
