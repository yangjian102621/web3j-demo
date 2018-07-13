pragma solidity ^0.4.4;

contract Voting {  // 使用contract关键字定义合约

  mapping (bytes32 => uint8) public votes; //使用映射表记录候选人得票数
  bytes32[] public candidates;    //使用定长数组记录候选人名单
  mapping(bytes32 => address[]) voteAddresses; //记录候选人的票仓

  //构造函数。参数：候选人名单
  constructor(bytes32[] candidateNames) public {
    candidates = candidateNames;
  }

  //获取指定候选人得票数。参数：候选人名称。返回值：8位无符号整数
  function getVotesFor(bytes32 candidate) view public returns (uint8) {
    require(validCandidate(candidate));//要求指定名称必须是有效候选人，否则停止执行
    return votes[candidate];
  }

  //获取指定候选人的支持者地址
  function getVotesAddresses(bytes32 candidate) public view returns (address[]){
      return voteAddresses[candidate];
  }

  //投票给指定候选人。参数：候选人名称
  function voteFor(bytes32 candidate) public {
    require(validCandidate(candidate));
    votes[candidate]  += 1;
    if (!containAddress(voteAddresses[candidate], msg.sender)) {
        voteAddresses[candidate].push(msg.sender);
    }
  }

  //检查指定的名称是否在候选人名单里。参数：候选人名称。返回值：true或false
  function validCandidate(bytes32 candidate) view public returns (bool) {
    for(uint i = 0; i < candidates.length; i++) {
      if (candidates[i] == candidate) {
        return true;
      }
    }
    return false;
   }

   //检查某个地址是否已经投过票了
   function containAddress(address[] addrs, address addr) pure private returns (bool) {
       for(uint i = 0; i < addrs.length; i++) {
          if (addrs[i] == addr) {
            return true;
          }
        }
        return false;
   }
}