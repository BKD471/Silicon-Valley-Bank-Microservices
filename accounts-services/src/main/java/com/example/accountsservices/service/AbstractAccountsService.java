package com.example.accountsservices.service;

import com.example.accountsservices.dto.*;
import com.example.accountsservices.exception.AccountsException;
import com.example.accountsservices.exception.BeneficiaryException;
import com.example.accountsservices.exception.CustomerException;
import com.example.accountsservices.exception.TransactionException;
import com.example.accountsservices.model.Accounts;
import com.example.accountsservices.model.Customer;
import com.example.accountsservices.repository.AccountsRepository;
import com.example.accountsservices.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;
import static com.example.accountsservices.service.impl.AccountsServiceImpl.REQUEST_TO_BLOCK;

//DESIGN NOTE
//Obeying I of SOLID ,to not pollute a single interface with too much methods
// by splitting it up.
//we want to have a single parent of all service classes so need a class to provide
// dummy implementation of all abstract methods of all interface
// so that we can spilt up service logic into separate concerning classes
// and thus obeying S of SOLID also

//since there is no need of instantiating the AbstractAccountsService ,
// it's just to provide dummy implementation ,so make it abstract
// it can be used for loose coupling or having some logic that will be used by all three service classes

public abstract class AbstractAccountsService implements IAccountsService, ITransactionsService, IBeneficiaryService {

    private  final  AccountsRepository accountsRepository;
    private  final CustomerRepository customerRepository;
    private  static final Accounts.AccountStatus STATUS_BLOCKED= Accounts.AccountStatus.BLOCKED;



    protected AbstractAccountsService(AccountsRepository accountsRepository,
                                      CustomerRepository customerRepository){
        this.accountsRepository=accountsRepository;
        this.customerRepository=customerRepository;
    }
    public OutputDto postRequestExecutor(InputDto postInputDto) throws AccountsException, CustomerException { return null;}
    public OutputDto  putRequestExecutor(InputDto putInputDto) throws AccountsException, CustomerException { return null;}
    public OutputDto getRequestExecutor(InputDto getInputDto) throws AccountsException, CustomerException { return null;}
    public OutputDto deleteRequestExecutor(InputDto deleteInputDto) throws AccountsException{ return null;}

    //ben
    public OutputDto postRequestBenExecutor(InputDto postInputDto) throws BeneficiaryException, AccountsException {return null;};
    public OutputDto putRequestBenExecutor(InputDto putInputDto) throws BeneficiaryException, AccountsException { return null;};
    public OutputDto getRequestBenExecutor(InputDto getInputDto) throws AccountsException, BeneficiaryException { return null;};
    public OutputDto deleteRequestBenExecutor(InputDto deleteInputDto) throws BeneficiaryException, AccountsException {return null;};

    //transactions
    public TransactionsDto transactionsExecutor(TransactionsDto transactionsDto) throws  TransactionException , AccountsException { return null;}
    public List<TransactionsDto> getPastSixMonthsTransactionsForAnAccount( Long accountNumber) throws AccountsException {return null;}
    protected Accounts fetchAccountByAccountNumber(Long accountNumber, String ...request) throws AccountsException {
        String methodName="fetchAccountByAccountNumber(Long,String vararg) in AbstractAccountsService";
        Optional<Accounts> fetchedAccounts = accountsRepository.findByAccountNumber(accountNumber);
        if (fetchedAccounts.isEmpty())
            throw new AccountsException(AccountsException.class,String.format("No such accounts exist with id %s", accountNumber),methodName);

        boolean checkAccountIsBlocked=STATUS_BLOCKED.equals(fetchedAccounts.get().getAccountStatus());
        if(request.length>0 && request[0].equalsIgnoreCase(REQUEST_TO_BLOCK) && checkAccountIsBlocked) throw new AccountsException(AccountsException.class,String.format("Account of id %s is already blocked",accountNumber),methodName);
        else if(checkAccountIsBlocked) throw new AccountsException(AccountsException.class,String.format("Account of id %s is in %s status",accountNumber,STATUS_BLOCKED),methodName);
        return fetchedAccounts.get();
    }

    protected Customer fetchCustomerByCustomerNumber(Long customerId) throws CustomerException{
        String methodName="fetchCustomerByCustomerNumber(Long)";
        Optional<Customer> loadCustomer=customerRepository.findById(customerId);
        if(loadCustomer.isEmpty()) throw  new CustomerException(CustomerException.class,String.format("No such customer with id %s exist",customerId),
                methodName);
        return loadCustomer.get();
    }
}
