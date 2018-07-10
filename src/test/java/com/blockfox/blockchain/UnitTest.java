package com.blockfox.blockchain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

/**
 * 以太坊的计量单位测试
 * 在org.web3j.utils.Convert类中，定义了枚举类型Unit来表示以太坊 的各种货币单位，例如：
 * Convert.Unit.WEI
 Convert.Unit.KWEI
 Convert.Unit.MWEI
 Convert.Unit.GWEI
 Convert.Unit.SZABO
 Convert.Unit.FINNEY
 Convert.Unit.ETHER
 Convert.Unit.KETHER
 Convert.Unit.METHER
 Convert.Unit.GETHER

 * @author yangjian
 * @since 2018-07-10 上午9:46.
 */
public class UnitTest {

	static Logger logger = LoggerFactory.getLogger(UnitTest.class);

	@Test
	public void main() {

		// 将一个 ether 转换为 wei
		BigDecimal oneEther = Convert.toWei("1",Convert.Unit.ETHER);

		// 将一个 wei 转换为 ether
		BigDecimal oneWei = Convert.fromWei("1",Convert.Unit.ETHER);

		logger.info("1 ether = "+oneEther+" wei");
		logger.info("1 wei = "+ oneWei+" ether");

	}
}
