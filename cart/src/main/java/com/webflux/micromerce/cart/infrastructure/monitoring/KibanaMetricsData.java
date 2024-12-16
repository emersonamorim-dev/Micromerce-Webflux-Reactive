package com.webflux.micromerce.cart.infrastructure.monitoring;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
public class KibanaMetricsData {
    @JsonProperty("@timestamp")
    private Instant timestamp = Instant.now();
    
    private long totalCarts = 0;
    private long activeCarts = 0;
    private long completedCarts = 0;
    private long abandonedCarts = 0;
    private long deletedCarts = 0;
    private long updatedCarts = 0;
    private long totalItems = 0;
    private long updatedItems = 0;
    private long averageItemsPerCart = 0;
    private long operationCount = 0;
    private long averageOperationTime = 0;
    private long errorCount = 0;
    
    // Métricas adicionais para o relatório
    private long cartsCreated = 0;
    private long cartsCheckedOut = 0;
    private double avgCheckoutTime = 0;
    private double avgItemAddTime = 0;
    private long checkoutCount = 0;
    private long itemAddCount = 0;
    private long itemUpdateCount = 0;
    private double avgItemUpdateTime = 0;

    public void recordCartCreated() {
        this.totalCarts++;
        this.activeCarts++;
        this.cartsCreated++;
    }

    public void recordCartCompleted() {
        this.completedCarts++;
        this.activeCarts--;
        this.cartsCheckedOut++;
    }

    public void recordCartAbandoned() {
        this.abandonedCarts++;
        this.activeCarts--;
    }

    public void recordCartDeleted() {
        this.deletedCarts++;
        this.totalCarts--;
        if (this.activeCarts > 0) {
            this.activeCarts--;
        }
    }

    public void recordCartUpdated() {
        this.updatedCarts++;
        this.operationCount++;
    }

    public void recordItemQuantityUpdated() {
        this.updatedItems++;
        this.itemUpdateCount++;
        this.operationCount++;
    }

    public void recordItemAdded(long operationTime) {
        this.totalItems++;
        this.itemAddCount++;
        if (activeCarts > 0) {
            this.averageItemsPerCart = this.totalItems / this.activeCarts;
        }
        this.avgItemAddTime = ((this.avgItemAddTime * (this.itemAddCount - 1)) + operationTime) / this.itemAddCount;
    }

    public void recordCheckout(long operationTime) {
        this.checkoutCount++;
        this.avgCheckoutTime = ((this.avgCheckoutTime * (this.checkoutCount - 1)) + operationTime) / this.checkoutCount;
    }

    public void recordOperation(long operationTime) {
        this.operationCount++;
        this.averageOperationTime = 
            ((this.averageOperationTime * (this.operationCount - 1)) + operationTime) 
            / this.operationCount;
    }

    public void recordError() {
        this.errorCount++;
    }
}
