package com.example.orderservice.jpa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;
    @Column(nullable = false)
    private Integer qty;
    @Column(nullable = false)
    private Integer unitPrice; // 단가
    @Column(nullable = false)
    private Integer totalPrice; // 수량 * 단가

    @Column(nullable = false)
    private String userId; // 주문자
    @Column(nullable = false, unique = true)
    private String orderId; // 주문 ID

    @Column(nullable = false, updatable = false, insertable = false)
    /* updatable, insertable 이 false 이기 때문에 default 값을 넣어줘야 한다. */
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date createdAt;
}
