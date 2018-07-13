package com.blackfox.blockchain.service.impl;

import com.blackfox.blockchain.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Account service
 * @author yangjian
 * @since 2018-07-09 上午11:28.
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

	static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	private Web3j web3j = Web3j.build(new HttpService("http://localhost:8545/"));

	@Override
	public ECKeyPair newAccount() throws Exception {

		return Keys.createEcKeyPair();
	}

	@Override
	public ECKeyPair getAddressByPrivateKey(String privateKeyStr) {
		//转换成 BigInt
		BigInteger privateKey = new BigInteger(privateKeyStr, 16);
		return ECKeyPair.create(privateKey);
	}

	@Override
	public List<String> getAccounts() throws IOException {

		return web3j.ethAccounts().send().getAccounts();
	}

	@Override
	public BigDecimal getBalance(String address) throws IOException {

		EthGetBalance response = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
		BigInteger balance = response.getBalance();
		return Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
	}

}
