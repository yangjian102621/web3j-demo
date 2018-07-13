package com.blackfox.blockchain.service;

import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Account service interface
 * @author yangjian
 * @since 2018-07-09 上午11:28.
 */
public interface AccountService {

	/**
	 * 创建钱包账号(秘钥对)
	 * @throws Exception
	 */
	ECKeyPair newAccount() throws Exception;

	/**
	 * 通过私钥来恢复账户和钱包地址
	 * @param privateKeyStr 私钥字符串
	 * @return
	 */
	ECKeyPair getAddressByPrivateKey(String privateKeyStr);

	/**
	 * 获取节点钱包地址列表
	 */
	List<String> getAccounts() throws IOException;

	/**
	 * 获取账户余额
	 * @param address
	 * @return
	 */
	BigDecimal getBalance(String address) throws IOException;

}
