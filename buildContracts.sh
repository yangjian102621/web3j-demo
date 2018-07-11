#!/usr/bin/env bash

set -e
set -o pipefail

# 合约代码根目录
srcDir=src/main/resources/solidity
# 合约编译输出目录
abiDir=$srcDir/build/
# java代码根目录
javaDir=src/main/java

# 逐个处理合约代码文件
for file in `ls $srcDir/*.sol`; do
	# 剔除代码文件名后缀
	target=$(basename $file .sol)

	# 编译合约代码
	echo "Compiling Solidity file ${target}.sol"

	solc --bin --abi --optimize --overwrite \
		--allow-paths "$(pwd)" \
		$file -o $abiDir
	echo "Complete"

	# 生成java包装类
	echo "Generating contract bindings"
	web3j solidity generate \
		$abiDir/$target.bin \
		$abiDir/$target.abi \
		-p com.hubwiz.demo.contracts \
		-o $javaDir > /dev/null
	echo "Complete"

done
