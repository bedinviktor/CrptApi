package com.ismp.crpt.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Document {
    private Description description;
    private String doc_id;
    private String doc_status;
    private String doc_type;
    private boolean importRequest;
    private String owner_inn;
    private String participant_inn;
    private String producer_inn;
    private LocalDate production_date;
    private String production_type;
    private List<Product> products;
    private LocalDate reg_date;
    private String reg_number;
}
