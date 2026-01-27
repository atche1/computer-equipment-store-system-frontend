package com.school.ppmg.computer_equipment_store_system_client.dtos.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> {
    private List<T> content;

    private int number;
    private int size;
    private int numberOfElements;

    private long totalElements;
    private int totalPages;

    private boolean first;
    private boolean last;
    private boolean empty;
}