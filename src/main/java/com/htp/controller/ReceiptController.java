package com.htp.controller;

import com.htp.controller.requests.ReceiptCreateRequest;
import com.htp.controller.requests.ReceiptResultRequest;
import com.htp.domain.hibernate.HibernateReceipt;
import com.htp.domain.hibernate.HibernateShipment;
import com.htp.domain.jdbc.Receipt;
import com.htp.repository.hibernate.HibernateReceiptDao;
import com.htp.repository.hibernate.HibernateShipmentDao;
import com.htp.repository.jdbc.CompaniesDao;
import com.htp.repository.jdbc.ProductCatalogDao;
import com.htp.repository.jdbc.UserDao;
import com.htp.repository.jdbc.impl.ReceiptDaoImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/rest/receipt")
public class ReceiptController {

    @Autowired
    @Qualifier("receiptDaoImpl")
    private ReceiptDaoImpl receiptDao;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Receipt>> getAll() {

        return new ResponseEntity<>(receiptDao.findAll(), HttpStatus.OK);
    }

    @Autowired
    private HibernateReceiptDao hibernateReceiptDaoImpl;

    @GetMapping("/all_hibernate_receipts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<HibernateReceipt>> getReceiptsHibernate() {
        return new ResponseEntity<>(hibernateReceiptDaoImpl.findAll(), HttpStatus.OK);
    }



    @GetMapping("/receipts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ReceiptResultRequest>> getReceipts() {

        return new ResponseEntity<>(receiptDao.findAllReceipts(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get receipt from server by id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful getting receipt"),
            @ApiResponse(code = 400, message = "Invalid receipt ID supplied"),
            @ApiResponse(code = 401, message = "Lol kek"),
            @ApiResponse(code = 404, message = "role was not found"),
            @ApiResponse(code = 500, message = "Server error, something wrong")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Receipt> getProductById(@ApiParam("HibernateReceipt Path Id") @PathVariable Long id) {
        Receipt receipt = receiptDao.findById(id);
        return new ResponseEntity<>(receipt, HttpStatus.OK);
    }


    @Autowired
    private UserDao userDao;

    @Autowired
    private ProductCatalogDao productCatalogDao;

    @Autowired
    private CompaniesDao companiesDao;



    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Receipt> createReceipt(@RequestBody ReceiptCreateRequest request) {
        var receipt = new Receipt();

        receipt.setReceiptUserId(userDao.findBySurname(request.getUserSurname()));
        receipt.setInvoiceNumber(request.getInvoiceNumber());
        receipt.setReceiptPrice(request.getReceiptPrice());
        receipt.setReceiptQuantity(request.getReceiptQuantity());
        receipt.setProductCatalogId(productCatalogDao.findByProductName(request.getProductCatalogName()));
        receipt.setSupplierId(companiesDao.findByCompanyName(request.getSupplier()));

        Receipt savedReceipt = receiptDao.save(receipt);

//        roleDao.findByRoleName(request.getRoleName().toLowerCase());

        return new ResponseEntity<>(savedReceipt, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> deleteReceipt(@PathVariable("id") Long receiptId) {
        receiptDao.delete(receiptId);
        return new ResponseEntity<>(receiptId, HttpStatus.OK);
    }
}
