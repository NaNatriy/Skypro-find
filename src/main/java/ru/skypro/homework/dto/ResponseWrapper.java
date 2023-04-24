package ru.skypro.homework.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseWrapper<T> {
    private Integer count;
    private List<T> results;
}
