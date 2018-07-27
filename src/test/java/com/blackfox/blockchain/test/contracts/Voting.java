package com.blackfox.blockchain.test.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class Voting extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5060405161056838038061056883398101604052805101805161003a906001906020840190610041565b50506100ab565b82805482825590600052602060002090810192821561007e579160200282015b8281111561007e5782518255602090920191600190910190610061565b5061008a92915061008e565b5090565b6100a891905b8082111561008a5760008155600101610094565b90565b6104ae806100ba6000396000f3006080604052600436106100775763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663155c0590811461007c5780632b38cd96146100e45780633477ee2e14610112578063351549861461013c578063392e667814610156578063b134790814610182575b600080fd5b34801561008857600080fd5b5061009460043561019a565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156100d05781810151838201526020016100b8565b505050509050019250505060405180910390f35b3480156100f057600080fd5b506100fc600435610213565b6040805160ff9092168252519081900360200190f35b34801561011e57600080fd5b5061012a600435610228565b60408051918252519081900360200190f35b34801561014857600080fd5b50610154600435610247565b005b34801561016257600080fd5b5061016e600435610394565b604080519115158252519081900360200190f35b34801561018e57600080fd5b506100fc6004356103e1565b60008181526002602090815260409182902080548351818402810184019094528084526060939283018282801561020757602002820191906000526020600020905b815473ffffffffffffffffffffffffffffffffffffffff1681526001909101906020018083116101dc575b50505050509050919050565b60006020819052908152604090205460ff1681565b600180548290811061023657fe5b600091825260209091200154905081565b61025081610394565b151561025b57600080fd5b600081815260208181526040808320805460ff8082166001011660ff19909116179055600282529182902080548351818402810184019094528084526102ed93928301828280156102e257602002820191906000526020600020905b815473ffffffffffffffffffffffffffffffffffffffff1681526001909101906020018083116102b7575b50505050503361040d565b151561034557600081815260026020908152604082208054600181018255908352912001805473ffffffffffffffffffffffffffffffffffffffff19163373ffffffffffffffffffffffffffffffffffffffff161790555b60408051828152905173ffffffffffffffffffffffffffffffffffffffff3316917fc1eff9d9e2ab8a2b29706e0c2818cd78972e60f1ce84c268a77005b0bece97c4919081900360200190a250565b6000805b6001548110156103d65760018054849190839081106103b357fe5b60009182526020909120015414156103ce57600191506103db565b600101610398565b600091505b50919050565b60006103ec82610394565b15156103f757600080fd5b5060009081526020819052604090205460ff1690565b6000805b8351811015610476578273ffffffffffffffffffffffffffffffffffffffff16848281518110151561043f57fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff16141561046e576001915061047b565b600101610411565b600091505b50929150505600a165627a7a723058203f2097fa34ef33357de44b46124a08449d33723a59f839f4e3ccf38ee4d6eb980029";

    public static final String FUNC_GETVOTESADDRESSES = "getVotesAddresses";

    public static final String FUNC_VOTES = "votes";

    public static final String FUNC_CANDIDATES = "candidates";

    public static final String FUNC_VOTEFOR = "voteFor";

    public static final String FUNC_VALIDCANDIDATE = "validCandidate";

    public static final String FUNC_GETVOTESFOR = "getVotesFor";

    public static final Event VOTE_EVENT = new Event("Vote", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
    ;

    protected Voting(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Voting(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<List> getVotesAddresses(byte[] candidate) {
        final Function function = new Function(FUNC_GETVOTESADDRESSES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(candidate)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<BigInteger> votes(byte[] param0) {
        final Function function = new Function(FUNC_VOTES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<byte[]> candidates(BigInteger param0) {
        final Function function = new Function(FUNC_CANDIDATES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<TransactionReceipt> voteFor(byte[] candidate) {
        final Function function = new Function(
                FUNC_VOTEFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(candidate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> validCandidate(byte[] candidate) {
        final Function function = new Function(FUNC_VALIDCANDIDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(candidate)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> getVotesFor(byte[] candidate) {
        final Function function = new Function(FUNC_GETVOTESFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(candidate)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<Voting> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, List<byte[]> candidateNames) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(candidateNames, org.web3j.abi.datatypes.generated.Bytes32.class))));
        return deployRemoteCall(Voting.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<Voting> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, List<byte[]> candidateNames) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(candidateNames, org.web3j.abi.datatypes.generated.Bytes32.class))));
        return deployRemoteCall(Voting.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<VoteEventResponse> getVoteEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(VOTE_EVENT, transactionReceipt);
        ArrayList<VoteEventResponse> responses = new ArrayList<VoteEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VoteEventResponse typedResponse = new VoteEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.voter = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.candidate = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<VoteEventResponse> voteEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, VoteEventResponse>() {
            @Override
            public VoteEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(VOTE_EVENT, log);
                VoteEventResponse typedResponse = new VoteEventResponse();
                typedResponse.log = log;
                typedResponse.voter = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.candidate = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<VoteEventResponse> voteEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VOTE_EVENT));
        return voteEventObservable(filter);
    }

    public static Voting load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Voting(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Voting load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Voting(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class VoteEventResponse {
        public Log log;

        public String voter;

        public byte[] candidate;
    }
}
