package by.kafkaMicroservice.kafkaMicroservice.service;

import by.kafkaMicroservice.kafkaMicroservice.dto.CreateProductDto;

import java.util.concurrent.ExecutionException;

public interface ProductService {
    String createProduct(CreateProductDto createProductDto) throws ExecutionException, InterruptedException;

}
