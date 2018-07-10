package com.blockfox.blockchain;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * @author yangjian
 * @since 2018-07-10 上午10:42.
 */
public class TxHelper {

	private Web3j web3j;
	List<String> accounts;

	public TxHelper() throws Exception{
		web3j = Web3j.build(new HttpService("http://localhost:8545"));
		accounts = web3j.ethAccounts().send().getAccounts();
	}

	public void getNodeAccountBalance(int idx) throws Exception {
		String account = web3j.ethAccounts().send().getAccounts().get(idx);
		DefaultBlockParameter block = DefaultBlockParameterName.LATEST;
		//DefaultBlockParameter block = new DefaultBlockParameterNumber(0);
		BigInteger balance = web3j.ethGetBalance(account,block).send().getBalance();
		System.out.println("balance " + idx + " @" + block.getValue() + ": " + balance );
	}

	public void convertUnit() {
		BigDecimal oneEther = Convert.toWei("1",Convert.Unit.ETHER);
		System.out.println("1 ether = " + oneEther + " wei");
		BigDecimal oneWei = Convert.fromWei("1",Convert.Unit.ETHER);
		System.out.println("1 wei = " + oneWei + " ether");
	}

	public String transactionOnly(int fromIndex,int toIndex,int amount) throws Exception {
		String from = accounts.get(fromIndex);
		String to = accounts.get(toIndex);
		BigInteger value = BigInteger.valueOf(amount);
		BigInteger gasPrice = null;
		BigInteger gasLimit = null;
		DefaultBlockParameter block = DefaultBlockParameterName.LATEST;
		String data = "null";
		BigInteger nonce = null;
		Transaction tx = new Transaction(from,nonce,gasPrice,gasLimit,to,value,data);
		String txHash = web3j.ethSendTransaction(tx).send().getTransactionHash();
		System.out.println("tx hash: " + txHash);
		return txHash;
	}

	private TransactionReceipt waitForTransactionReceipt(String txHash,long timeout) throws Exception {
		System.out.println("wait for receipt...");
		long t0 = System.currentTimeMillis();
		Optional<TransactionReceipt> receipt = null;
		while(true){
			receipt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();
			if(receipt.isPresent()) {
				System.out.println("got receipt");
				return receipt.get();
			}
			long t1 = System.currentTimeMillis();
			if((t1-t0) > timeout) {
				System.out.println("time out");
				return null;
			}
			Thread.sleep(100);
		}
	}

	public void transactionWithReceipt(int fromIndex,int toIndex,int amount) throws Exception {
		String txHash = transactionOnly(fromIndex,toIndex,amount);
		TransactionReceipt receipt = waitForTransactionReceipt(txHash,  2*1000);
		System.out.println("tx receipt =>");
		System.out.println("tx hash: " + receipt.getTransactionHash());
		System.out.println("tx index: " + receipt.getTransactionIndex());
		System.out.println("block hash: " + receipt.getBlockHash());
		System.out.println("block number: " + receipt.getBlockNumber());
		System.out.println("cumulativeGasUsed: " + receipt.getCumulativeGasUsed());
		System.out.println("gas used: " + receipt.getGasUsed());
		System.out.println("contractAddress: " + receipt.getContractAddress());
		System.out.println("root: " + receipt.getRoot());
		System.out.println("status: " + receipt.getStatus());
		System.out.println("from: " + receipt.getFrom());
		System.out.println("to: " + receipt.getTo());
		System.out.println("logs: " + receipt.getLogs());
		System.out.println("logsBloom: " + receipt.getLogsBloom());
	}

	public void transactionWithGas(int fromIndex,int toIndex,int amount) throws Exception {
		String from = accounts.get(fromIndex);
		String to = accounts.get(toIndex);
		BigInteger value = BigInteger.valueOf(amount);
		BigInteger gasPrice = Convert.toWei("20",Convert.Unit.GWEI).toBigInteger();
		BigInteger gasLimit = BigInteger.valueOf(30000);
		DefaultBlockParameter block = DefaultBlockParameterName.LATEST;
		String data = "null";
		BigInteger nonce = null;
		Transaction tx = new Transaction(from,nonce,gasPrice,gasLimit,to,value,data);
		String txHash = web3j.ethSendTransaction(tx).send().getTransactionHash();
		System.out.println("tx hash: " + txHash);
		TransactionReceipt receipt = waitForTransactionReceipt(txHash,  2*1000);
		System.out.println("tx receipt => " + receipt);
	}

	public void transactionWithData(int accountIndex,String data) throws Exception {
		String from = accounts.get(accountIndex);
		String to = accounts.get(accountIndex);
		BigInteger value = null;
		BigInteger gasPrice = null;
		BigInteger gasLimit = null;
		BigInteger nonce = null;
		Transaction tx = new Transaction(from,nonce,gasPrice,gasLimit,to,value,data);
		String txHash = web3j.ethSendTransaction(tx).send().getTransactionHash();
		System.out.println("tx hash: " + txHash);
		TransactionReceipt receipt = waitForTransactionReceipt(txHash,  2*1000);
		System.out.println("tx receipt => " + receipt);
	}

	public void transferToWallet() throws Exception{
		web3j = Web3j.build(new HttpService("http://localhost:8545"));
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		Credentials credentials = WalletUtils.loadCredentials("123","./keystore/UTC--2018-06-18T08-33-26.840986000Z--7d06fc6d803ccd43b90429c2fcfb37f33bbd3309.json");
		String from = accounts.get(0);
		String to = credentials.getAddress();
		BigInteger nonce = null;
		BigInteger gasPrice = null;
		BigInteger gasLimit = null;
		BigInteger value = Convert.toWei("1",Convert.Unit.ETHER).toBigInteger();
		String data = null;
		Transaction tx = new Transaction(from,nonce,gasPrice,gasLimit,to,value,data);
		String txHash = web3j.ethSendTransaction(tx).send().getTransactionHash();
		System.out.println("tx hash: " + txHash);
		TransactionReceipt receipt = waitForTransactionReceipt(txHash,  2*1000);
		System.out.println("tx receipt => " + receipt);
	}

	public void rawTransaction() throws Exception {
		Credentials credentials = WalletUtils.loadCredentials("123","./keystore/UTC--2018-06-18T08-33-26.840986000Z--7d06fc6d803ccd43b90429c2fcfb37f33bbd3309.json");
		String from = credentials.getAddress();
		String to = from;
		BigInteger gasPrice = BigInteger.valueOf(22000000000L);
		BigInteger gasLimit = BigInteger.valueOf(6700000L);
		BigInteger value = BigInteger.valueOf(0);
		String data = "787878";
		BigInteger nonce = web3j.ethGetTransactionCount(from,DefaultBlockParameterName.LATEST).send().getTransactionCount();
		RawTransaction rawTx = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,to,value,data);
		byte[] signedMessage = TransactionEncoder.signMessage(rawTx,credentials);
		String hexValue = Numeric.toHexString(signedMessage);
		String txHash = web3j.ethSendRawTransaction(hexValue).send().getTransactionHash();
		TransactionReceipt receipt = waitForTransactionReceipt(txHash,  2*1000);
		System.out.println("tx receipt => " + receipt);
	}

	public void transferMoney(int fromIndex,int toIndex,int amount) throws Exception {
		web3j = Web3j.build(new HttpService("http://localhost:8545"));
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		String from = accounts.get(fromIndex);
		String to = accounts.get(toIndex);
		BigDecimal value = BigDecimal.valueOf(amount);
		ClientTransactionManager ctm = new ClientTransactionManager(web3j,from);
		Transfer transfer = new Transfer(web3j,ctm);
		System.out.println("transfer...");
		TransactionReceipt receipt = transfer.sendFunds(to,value, Convert.Unit.WEI).send();
		System.out.println("receipt => " + receipt);
	}
	public void transferMoneyRaw(int toIndex,int amount) throws Exception {
		web3j = Web3j.build(new HttpService("http://localhost:8545"));
		List<String> accounts = web3j.ethAccounts().send().getAccounts();
		Credentials credentials = WalletUtils.loadCredentials("123","./keystore/UTC--2018-06-18T08-33-26.840986000Z--7d06fc6d803ccd43b90429c2fcfb37f33bbd3309.json");
		String to = accounts.get(toIndex);
		BigDecimal value = BigDecimal.valueOf(amount);
		RawTransactionManager ctm = new RawTransactionManager(web3j,credentials);
		Transfer transfer = new Transfer(web3j,ctm);
		System.out.println("raw transfer...");
		TransactionReceipt receipt = transfer.sendFunds(to,value,Convert.Unit.WEI).send();
		System.out.println("receipt => " + receipt);
	}

}
