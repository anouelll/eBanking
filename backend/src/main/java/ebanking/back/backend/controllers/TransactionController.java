package ebanking.back.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ebanking.back.backend.model.TransactionEvent;
import ebanking.back.backend.services.TransactionPublisher;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

     private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionPublisher transactionPublisher;

    public TransactionController(TransactionPublisher transactionPublisher) {
        this.transactionPublisher = transactionPublisher;
    }

    @PostMapping("/send")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionEvent transactionEvent) {
        transactionPublisher.sendTransactionMessage(transactionEvent);
        logger.info("Transaction published: {}", transactionEvent);
        return ResponseEntity.ok("Transaction sent successfully");
    }
}
