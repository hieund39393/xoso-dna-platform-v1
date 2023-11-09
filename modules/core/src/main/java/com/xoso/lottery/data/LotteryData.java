package com.xoso.lottery.data;

import com.xoso.lottery.model.Lottery;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Setter
@Getter
public class LotteryData {
    public Long id;
    public String name;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    public static LotteryData fromEntity(Lottery entity){
        return LotteryData.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdDate(entity.getCreatedDate())
                .modifiedDate(entity.getModifiedDate())
                .build();
    }
}
