package com.example.accountsservices.service;

import com.example.accountsservices.dto.AccountsDto;
import com.example.accountsservices.dto.inputDtos.DeleteInputRequestDto;
import com.example.accountsservices.dto.inputDtos.GetInputRequestDto;
import com.example.accountsservices.dto.inputDtos.PostInputRequestDto;
import com.example.accountsservices.dto.inputDtos.PutInputRequestDto;
import com.example.accountsservices.dto.outputDtos.OutputDto;
import com.example.accountsservices.exception.AccountsException;
import com.example.accountsservices.exception.CustomerException;
import com.example.accountsservices.helpers.CodeRetrieverHelper;
import com.example.accountsservices.model.Accounts;
import com.example.accountsservices.model.Customer;
import com.example.accountsservices.repository.AccountsRepository;
import com.example.accountsservices.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountsServiceTests {
    @Qualifier("accountsServicePrimary")
    @Autowired
    private IAccountsService accountsService;

    @Qualifier("fileServicePrimary")
    @Autowired
    private IFileService fileService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private AccountsRepository accountsRepository;

    Customer customer;
    Accounts accounts;

    private final int MAX_PERMISSIBLE_ACCOUNTS = 5;

    @Value("${customer.profile.images.path}")
    private String IMAGE_PATH;

    @BeforeEach
    public void setUp() {
        String branchCode = CodeRetrieverHelper.getBranchCode(Accounts.Branch.KOLKATA);
        accounts = Accounts.builder()
                .accountNumber(1L)
                .accountType(Accounts.AccountType.SAVINGS)
                .accountStatus(Accounts.AccountStatus.OPEN)
                .anyActiveLoans(false)
                .approvedLoanLimitBasedOnCreditScore(500000L)
                .balance(60000L)
                .branchCode(branchCode)
                .totalOutStandingAmountPayableToBank(500000L)
                .transferLimitPerDay(25000L)
                .totLoanIssuedSoFar(450000L)
                .creditScore(750)
                .homeBranch(Accounts.Branch.KOLKATA)
                .build();

        accounts.setCreatedDate(LocalDate.of(1990,12,01));

        customer = Customer.builder()
                .customerId(1L)
                .age(25)
                .name("phoenix")
                .email("phoenix@gmail.com")
                .phoneNumber("+91-9876543217")
                .address("address")
                .adharNumber("adhar")
                .drivingLicense("driving")
                .panNumber("pan")
                .passportNumber("passport")
                .imageName("img.png")
                .DateOfBirth(LocalDate.of(1997, 12, 01))
                .voterId("voter")
                .accounts(Collections.singletonList(accounts))
                .build();
        accounts.setCustomer(customer);
    }


    @Test
    @DisplayName("Test the create accounts")
    public void createAccountTest() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        when(customerRepository.save(any())).thenReturn(customer);

        String branchCode = CodeRetrieverHelper.getBranchCode(Accounts.Branch.KOLKATA);
        PostInputRequestDto postInputRequestDto = PostInputRequestDto.builder()
                .updateRequest(AccountsDto.UpdateRequest.CREATE_ACC)
                .name("phoenix")
                .email("phoenix@gmail.com")
                .password("pass")
                .phoneNumber("91-1234567890")
                .homeBranch(Accounts.Branch.KOLKATA)
                .dateOfBirthInYYYYMMDD(String.valueOf(LocalDate.of(1997, 12, 01)))
                .adharNumber("adhar")
                .panNumber("pan")
                .voterId("voter")
                .address("address")
                .drivingLicense("driving")
                .passportNumber("passport")
                .accountType(Accounts.AccountType.SAVINGS)
                .branchCode(branchCode)
                .transferLimitPerDay(25000L)
                .creditScore(750)
                .age(25)
                .build();
        OutputDto response = accountsService.accountSetUp(postInputRequestDto);

        assertEquals("phoenix@gmail.com", response.getCustomer().getEmail(),"Customer Email should have matched");
        assertEquals("passport", response.getCustomer().getPassportNumber(),"Customer Passport should have matched");
        assertEquals("address", response.getCustomer().getAddress(),"Customer address should have matched");
        assertEquals("adhar", response.getCustomer().getAdharNumber(),"Customer adhar should have matched");
        assertEquals("voter", response.getCustomer().getVoterId(),"Customer voter should have matched");
        assertEquals("driving", response.getCustomer().getDrivingLicense(),"Customer driving should have matched");
        assertEquals("pan", response.getCustomer().getPanNumber(),"Customer pan should have matched");
        assertEquals(25, response.getCustomer().getAge(),"Customer age should have matched");
        assertEquals(LocalDate.of(1997, 12, 01), response.getCustomer().getDateOfBirth(),
                "Customer dob should have matched");
        assertEquals(60000L, response.getAccounts().getBalance(),"Customer balance should have matched");
        assertEquals(25000L, response.getAccounts().getTransferLimitPerDay(),"Customer transferLimit should have matched");
        assertEquals(750, response.getAccounts().getCreditScore(),"Customer credit score should have matched");
    }

    @Test
    @DisplayName("Test add accounts")
    public void addAccountTest() throws IOException {
        String branchCode = CodeRetrieverHelper.getBranchCode(Accounts.Branch.CHENNAI);
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        Accounts processedAccount = Accounts.builder()
                .accountNumber(2L)
                .accountType(Accounts.AccountType.SAVINGS)
                .accountStatus(Accounts.AccountStatus.OPEN)
                .anyActiveLoans(false)
                .approvedLoanLimitBasedOnCreditScore(900000L)
                .balance(90000L)
                .branchCode(branchCode)
                .totalOutStandingAmountPayableToBank(500000L)
                .transferLimitPerDay(85000L)
                .totLoanIssuedSoFar(550000L)
                .creditScore(850)
                .homeBranch(Accounts.Branch.CHENNAI)
                .build();

        when(accountsRepository.save(any())).thenReturn(processedAccount);


        PutInputRequestDto putInputRequestDto = PutInputRequestDto.builder()
                .customerId(1L)
                .accountType(Accounts.AccountType.SAVINGS)
                .updateRequest(AccountsDto.UpdateRequest.ADD_ACCOUNT)
                .homeBranch(Accounts.Branch.CHENNAI)
                .build();
        OutputDto response = accountsService.putRequestExecutor(putInputRequestDto);
        assertEquals(850, response.getAccounts().getCreditScore(),"Account CreditScore should have matched");
        assertEquals(90000L, response.getAccounts().getBalance(),"Account Balance should have matched");
        assertEquals(Accounts.Branch.CHENNAI, response.getAccounts().getHomeBranch(),"Account Branch should have matched");
    }

    @Test
    @DisplayName("Adding accounts failed when no of accounts exceeds the permissible limit")
    public void addAccountValidationForMaxPermissibleAccountTest() throws IOException {
        String branchCode = CodeRetrieverHelper.getBranchCode(Accounts.Branch.CHENNAI);
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        List<Accounts> accountsList = new ArrayList<>();
        for (int i = 0; i < MAX_PERMISSIBLE_ACCOUNTS; i++) {
            accountsList.add(new Accounts());
        }
        customer.setAccounts(accountsList);
        PutInputRequestDto putInputRequestDto = PutInputRequestDto.builder()
                .customerId(1L)
                .accountType(Accounts.AccountType.SAVINGS)
                .updateRequest(AccountsDto.UpdateRequest.ADD_ACCOUNT)
                .homeBranch(Accounts.Branch.CHENNAI)
                .build();
        assertThrows(AccountsException.class, () -> {
            accountsService.putRequestExecutor(putInputRequestDto);
        },"AccountsException should have been thrown");

    }

    @Test
    @DisplayName("Adding accounts failed for invalid Customer Id")
    public void AddAccountFailedForInvalidCustomerIdTest() throws IOException {
        String branchCode = CodeRetrieverHelper.getBranchCode(Accounts.Branch.CHENNAI);
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer)).thenReturn(Optional.empty());

        PutInputRequestDto putInputRequestDto = PutInputRequestDto.builder()
                .customerId(1L)
                .accountType(Accounts.AccountType.SAVINGS)
                .updateRequest(AccountsDto.UpdateRequest.ADD_ACCOUNT)
                .homeBranch(Accounts.Branch.CHENNAI)
                .build();
        assertThrows(AccountsException.class,
                () -> {
                    accountsService.putRequestExecutor(putInputRequestDto);
                },"AccountsException should have been thrown");
    }

    @Test
    @DisplayName("Test update home branch")
    public void updateHomeBranchTest() throws IOException {
        String newBranchCode = CodeRetrieverHelper.getBranchCode(Accounts.Branch.BANGALORE);

        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        PutInputRequestDto putInputRequestDto = PutInputRequestDto.builder()
                .accountNumber(1L)
                .homeBranch(Accounts.Branch.BANGALORE)
                .updateRequest(AccountsDto.UpdateRequest.UPDATE_HOME_BRANCH).build();

        Accounts savedAccount = Accounts.builder()
                .accountNumber(1L)
                .homeBranch(Accounts.Branch.BANGALORE)
                .branchCode(newBranchCode)
                .accountType(Accounts.AccountType.CURRENT)
                .build();
        savedAccount.setCustomer(customer);
        when(accountsRepository.save(any())).thenReturn(savedAccount);
        OutputDto response = accountsService.putRequestExecutor(putInputRequestDto);

        assertEquals(Accounts.Branch.BANGALORE, response.getAccounts().getHomeBranch(),"Accounts Branch should have matched");
        assertEquals(newBranchCode, response.getAccounts().getBranchCode(),"Account Branch COde should have matched");
    }

    @Test
    @DisplayName("Update home branch failed when there is already another account with same type ")
    public void updateHomeBranchFailedTest() throws IOException {
        String newBranchCode = CodeRetrieverHelper.getBranchCode(Accounts.Branch.KOLKATA);

        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        PutInputRequestDto putInputRequestDto = PutInputRequestDto.builder()
                .accountNumber(1L)
                .homeBranch(Accounts.Branch.KOLKATA)
                .updateRequest(AccountsDto.UpdateRequest.UPDATE_HOME_BRANCH).build();

        assertThrows(AccountsException.class, () -> {
            accountsService.putRequestExecutor(putInputRequestDto);
        },"AccountsException should have been thrown");
    }

    @Test
    @DisplayName("Loading customer Failed for invalid customerId")
    public void invalidCustomerIdFailedTest() throws IOException {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        PutInputRequestDto putInputRequestDto = PutInputRequestDto.builder().customerId(1L).build();
        assertThrows(CustomerException.class,
                () -> {
                    accountsService.putRequestExecutor(putInputRequestDto);
                },"Customer Exception not being thrown");
    }

    @Test
    @DisplayName("Loading account failed for invalid accountNumber")
    public void invalidAccountNumberFailedTest() throws IOException {
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.empty());
        PutInputRequestDto putInputRequestDto = PutInputRequestDto.builder().accountNumber(47L).build();
        assertThrows(AccountsException.class,
                () -> {
                    accountsService.putRequestExecutor(putInputRequestDto);
                },"AccountsException should have been thrown");
    }

    @Test
    @DisplayName("Test update customer details")
    public void updateCustomerDataTest() throws IOException {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        Customer updatedCustomer= Customer.builder()
                .customerId(1L)
                .name("Updated Name")
                .email("updated@gmail.com")
                .phoneNumber("91-9345678912")
                .adharNumber("1234-5678-9034")
                .panNumber("GMDPD1234H")
                .voterId("vtdindeqpfc")
                .address("updated address")
                .drivingLicense("HR-0619441199191")
                .passportNumber("U6325787")
                .DateOfBirth(LocalDate.of(2000, 01, 02)).build();

        when(customerRepository.save(any())).thenReturn(updatedCustomer);
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(1, 2, sort);
        List<Accounts> accountsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) accountsList.add(new Accounts());
        Page<Accounts> allPagedAccounts = new PageImpl<>(accountsList);
        when(accountsRepository.findAllByCustomer_CustomerId(anyLong(),any(Pageable.class))).thenReturn(Optional.of(allPagedAccounts));

        PutInputRequestDto request = PutInputRequestDto.builder()
                .customerId(1L)
                .updateRequest(AccountsDto.UpdateRequest.UPDATE_CUSTOMER_DETAILS)
                .name("Updated Name")
                .email("updated@gmail.com")
                .phoneNumber("91-9345678912")
                .adharNumber("1234-5678-9034")
                .panNumber("GMDPD1234H")
                .voterId("vtdindeqpfc")
                .address("updated address")
                .drivingLicense("HR-0619441199191")
                .passportNumber("U6325787")
                .dateOfBirthInYYYYMMDD(String.valueOf(LocalDate.of(2000, 01, 02)))
                .build();

        OutputDto response = accountsService.putRequestExecutor(request);
        assertNotNull(response.getCustomer(),"Customer should not be null");
        assertEquals(response.getCustomer().getEmail(),request.getEmail(),"Customer Email should have updated");
        assertEquals(response.getCustomer().getCustomerName(),request.getName(),"Customer Name should have updated");
        assertEquals(response.getCustomer().getPhoneNumber(),request.getPhoneNumber(),"Customer Phone Number should have updated");
        assertEquals(response.getCustomer().getAdharNumber(),request.getAdharNumber(),"Customer Adhar Number should have updated");
        assertEquals(response.getCustomer().getPanNumber(),request.getPanNumber(),"Customer Pan Number should have updated");
        assertEquals(response.getCustomer().getVoterId(),request.getVoterId(),"Customer Voter Id should have updated");
        assertEquals(response.getCustomer().getAddress(),request.getAddress(),"Customer Address should have updated");
        assertEquals(response.getCustomer().getDrivingLicense(),request.getDrivingLicense(),"Customer DrivingLicense should have updated");
    }

    @Test
    @DisplayName("Test upload profile image")
    public void uploadProfileImageTest() throws IOException{
        UUID imageId = UUID.randomUUID();
        mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(imageId);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        Customer customerWithUploadedImage=Customer.builder()
                .customerId(1L)
                .imageName(imageId.toString())
                .build();

        when(customerRepository.save(any())).thenReturn(customerWithUploadedImage);


        MockMultipartFile imgfile =
                new MockMultipartFile("data", "uploadedfile.png", "text/plain", "some kml".getBytes());
        PutInputRequestDto request= PutInputRequestDto.builder()
                .customerId(1L)
                .updateRequest(AccountsDto.UpdateRequest.UPLOAD_CUSTOMER_IMAGE)
                .customerImage(imgfile)
                .build();

        OutputDto response=accountsService.putRequestExecutor(request);
        assertEquals(customerWithUploadedImage.getImageName()+".png",response.getCustomer().getImageName(),
                "Image should have been uploaded");
       verify(customerRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("Test block account")
    public void blockAccountTest() throws AccountsException, IOException {
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        Accounts blockedAccnt=Accounts.builder()
                .accountNumber(1L)
                .accountStatus(Accounts.AccountStatus.BLOCKED)
                .build();
        when(accountsRepository.save(any())).thenReturn(blockedAccnt);

        PutInputRequestDto request= PutInputRequestDto.builder()
                .accountNumber(1L)
                .updateRequest(AccountsDto.UpdateRequest.BLOCK_ACC)
                .build();

        accountsService.putRequestExecutor(request);
        verify(accountsRepository,times(1)).save(accounts);
    }

    @Test
    @DisplayName("Failed blocking account test")
    public void blockAccountFailedTest() throws AccountsException, IOException {
        Accounts blockedAccounts= accounts;
        blockedAccounts.setAccountStatus(Accounts.AccountStatus.BLOCKED);
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(blockedAccounts));

        PutInputRequestDto request= PutInputRequestDto.builder()
                .accountNumber(1L)
                .updateRequest(AccountsDto.UpdateRequest.BLOCK_ACC)
                .build();

        assertThrows(AccountsException.class,()->{accountsService.putRequestExecutor(request);});
    }

    @Test
    @DisplayName("Test close account")
    public void closeAccountTest() throws AccountsException, IOException {
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        Accounts closedAccount=Accounts.builder()
                .accountNumber(1L)
                .accountStatus(Accounts.AccountStatus.CLOSED)
                .build();
        when(accountsRepository.save(any())).thenReturn(closedAccount);

        PutInputRequestDto request= PutInputRequestDto.builder()
                .accountNumber(1L)
                .updateRequest(AccountsDto.UpdateRequest.CLOSE_ACC)
                .build();

        accountsService.putRequestExecutor(request);
        verify(accountsRepository,times(1)).save(accounts);
    }

    @Test
    @DisplayName("Test the reopening of closed account")
    public void reOpenClosedAccountTest() throws AccountsException, IOException {

        Accounts openedAccount=Accounts.builder()
                .accountNumber(1L)
                .accountStatus(Accounts.AccountStatus.OPEN)
                .build();
        Accounts closedAccount=Accounts.builder()
                .accountNumber(1L)
                .accountStatus(Accounts.AccountStatus.CLOSED)
                .build();
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(closedAccount));
        when(accountsRepository.save(any())).thenReturn(openedAccount);

        PutInputRequestDto request= PutInputRequestDto.builder()
                .accountNumber(1L)
                .updateRequest(AccountsDto.UpdateRequest.RE_OPEN_ACC)
                .accountStatus(Accounts.AccountStatus.CLOSED)
                .build();

        accountsService.putRequestExecutor(request);
        verify(accountsRepository,times(1)).save(closedAccount);
    }

    @Test
    @DisplayName("Test delete account")
    public void deleteAccountTest() throws AccountsException, IOException {
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));

        DeleteInputRequestDto request= DeleteInputRequestDto.builder()
                .accountNumber(1L)
                .updateRequest(AccountsDto.UpdateRequest.DELETE_ACC)
                .accountStatus(Accounts.AccountStatus.OPEN)
                .build();

        accountsService.deleteRequestExecutor(request);
        verify(accountsRepository,times(1)).deleteByAccountNumber(1L);
    }


    @Test
    @DisplayName("Test fetching the account information")
    public  void getAccountInfoTest() throws AccountsException,IOException{
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        GetInputRequestDto request=GetInputRequestDto.builder()
                .accountNumber(1L)
                .updateRequest(AccountsDto.UpdateRequest.GET_ACC_INFO)
                .build();
        OutputDto response=accountsService.getRequestExecutor(request);
        assertNotNull(response.getAccounts(),"Accounts should nt be null");
        assertEquals(accounts.getAccountNumber(),response.getAccounts().getAccountNumber(),
                "Account NUmber should also be equal");
    }

    @Test
    @DisplayName("Create Account Failed coz Another account with different customer has same credentials")
    public void createAccountFailedTest() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        when(customerRepository.save(any())).thenReturn(customer);

        String branchCode = CodeRetrieverHelper.getBranchCode(Accounts.Branch.KOLKATA);
        PostInputRequestDto postInputRequestDto = PostInputRequestDto.builder()
                .updateRequest(AccountsDto.UpdateRequest.CREATE_ACC)
                .adharNumber("adhar")
                .build();

        Customer customerWithDuplicateCredentials=Customer.builder()
                .adharNumber("adhar")
                .build();
        Accounts accountsWithDuplicatedCredentials=Accounts.builder()
                .accountNumber(3L)
                .customer(customerWithDuplicateCredentials)
                .build();
        List<Accounts> duplicateAccountThatWillCauseException=Collections.singletonList(accountsWithDuplicatedCredentials);
        when(accountsRepository.findAll()).thenReturn(duplicateAccountThatWillCauseException);
        assertThrows(AccountsException.class,()->{
             accountsService.accountSetUp(postInputRequestDto);
        });
    }

    @Test
    @DisplayName("Test the increment of transfer limit per day")
    public void increaseTransferLimitPerDayTest() throws IOException {
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        PutInputRequestDto request= PutInputRequestDto.builder()
                .accountNumber(1L)
                .updateRequest(AccountsDto.UpdateRequest.INC_TRANSFER_LIMIT)
                .transferLimitPerDay(125000L)
                .build();

        Accounts savedAccount=Accounts.builder()
                .accountNumber(1L)
                .transferLimitPerDay(125000L)
                .build();
        when(accountsRepository.save(any())).thenReturn(savedAccount);
        OutputDto response=accountsService.putRequestExecutor(request);
        assertEquals(125000L,response.getAccounts().getTransferLimitPerDay(),"Transfer limit should have updated");
    }

    @Test
    @DisplayName("Transfer Limit failed for accounts less than six months old")
    public void increaseTransferLimitPerDayFailedTest() throws IOException {
        accounts.setCreatedDate(LocalDate.now());
        when(accountsRepository.findByAccountNumber(anyLong())).thenReturn(Optional.of(accounts));
        PutInputRequestDto request= PutInputRequestDto.builder()
                .accountNumber(1L)
                .updateRequest(AccountsDto.UpdateRequest.INC_TRANSFER_LIMIT)
                .transferLimitPerDay(125000L)
                .build();


        assertThrows(AccountsException.class,()->{
            accountsService.putRequestExecutor(request);
        },"Should have thrown accounts Exception");
    }

    @Test
    @DisplayName("Delete all accounts by customer")
    public void deleteAllAccountsByCustomerTest(){
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        DeleteInputRequestDto request=DeleteInputRequestDto.builder()
                .updateRequest(AccountsDto.UpdateRequest.DELETE_ALL_ACC)
                .customerId(1L)
                .build();

        accountsService.deleteRequestExecutor(request);
        verify(accountsRepository,times(1)).deleteAllByCustomer_CustomerId(anyLong());
    }

    @Test
    @DisplayName("Get all accounts")
    public void getAllAccTest() throws AccountsException,IOException{
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(1, 2, sort);
        List<Accounts> accountsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) accountsList.add(new Accounts());
        Page<Accounts> allPagedAccounts = new PageImpl<>(accountsList);
        when(accountsRepository.findAllByCustomer_CustomerId(anyLong(),any(Pageable.class))).thenReturn(Optional.of(allPagedAccounts));

        GetInputRequestDto request= GetInputRequestDto.builder()
                .customerId(1L)
                .updateRequest(AccountsDto.UpdateRequest.GET_ALL_ACC)
                .build();

        accountsService.getRequestExecutor(request);
        verify(accountsRepository,times(1))
                .findAllByCustomer_CustomerId(anyLong(),any(Pageable.class));
    }

    @Test
    @DisplayName("Invalid request type for get")
    public  void invalidGetRequestType() throws IOException {
        GetInputRequestDto request= GetInputRequestDto.builder().updateRequest(AccountsDto.UpdateRequest.ADD_ACCOUNT).build();
        assertThrows(AccountsException.class,()->{
            accountsService.getRequestExecutor(request);
        });
    }

    @Test
    @DisplayName("Invalid request type for put")
    public  void invalidPutRequestType() throws IOException {
        PutInputRequestDto request= PutInputRequestDto.builder().updateRequest(AccountsDto.UpdateRequest.GET_ACC_INFO).build();
        assertThrows(AccountsException.class,()->{
            accountsService.putRequestExecutor(request);
        });
    }

    @Test
    @DisplayName("Invalid request type for post")
    public  void invalidPostRequestType() throws IOException {
        PostInputRequestDto request= PostInputRequestDto.builder().updateRequest(AccountsDto.UpdateRequest.DELETE_ACC).build();
        assertThrows(AccountsException.class,()->{
            accountsService.postRequestExecutor(request);
        });
    }

    @Test
    @DisplayName("Invalid request type for delete")
    public  void invalidDeleteRequestType() throws IOException {
        DeleteInputRequestDto request= DeleteInputRequestDto.builder().updateRequest(AccountsDto.UpdateRequest.UPDATE_CREDIT_SCORE).build();
        assertThrows(AccountsException.class,()->{
            accountsService.deleteRequestExecutor(request);
        });
    }

}