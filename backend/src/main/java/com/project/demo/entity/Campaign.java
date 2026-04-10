package com.project.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data // Của Lombok: Tự sinh getter, setter, toString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Tên chiến dịch (VD: Flash Sale 11/11)

    @Column(nullable = false)
    private Integer totalItems; // Tổng số quà tặng/sản phẩm

    @Column(nullable = false)
    private Integer availableItems; // Số lượng còn lại

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Trạng thái: DRAFT, ACTIVE, ENDED
    private String status;

    @Version
    private Long version;
}