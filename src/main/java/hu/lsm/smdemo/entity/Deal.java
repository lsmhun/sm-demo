package hu.lsm.smdemo.entity;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Deal {

    private UUID id;
    private long timestamp;
    private String user;
    private Double price;

}
