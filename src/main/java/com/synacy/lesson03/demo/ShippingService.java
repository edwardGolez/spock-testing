package com.synacy.lesson03.demo;

import com.synacy.lesson03.demo.domain.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShippingService {

	private SalesOrderService salesOrderService;
	private WarehouseService warehouseService;
	private CustomerNotificationService customerNotificationService;

	public List<SalesOrder> shipSalesOrderByBatch(ShippingBatch shippingBatch) {

		List<SalesOrder> salesOrderForShipment = salesOrderService.fetchSalesOrderDueForShipment(shippingBatch);

		Map<FinishedGood, Integer> totalItemsShipped = new HashMap<>();
		for(SalesOrder salesOrder: salesOrderForShipment) {
			Map<FinishedGood, Integer> salesOrderItemCount = salesOrder.getSalesOrderItemCount();
			incrementTotalItemsShipped(totalItemsShipped, salesOrderItemCount);

			salesOrderService.updateStatus(salesOrder, SalesOrderStatus.SHIPPED);
			customerNotificationService.notifyCustomer(NotificationType.SALES_ORDER_STATUS, salesOrder);
		}

		warehouseService.updateInventoryLevel(totalItemsShipped);

		return salesOrderForShipment;
	}

	private void incrementTotalItemsShipped(Map<FinishedGood, Integer> totalItemsShipped, Map<FinishedGood, Integer> salesOrderItemCount) {
		Iterator<FinishedGood> iterator = salesOrderItemCount.keySet().iterator();

		while(iterator.hasNext()) {
			FinishedGood finishedGood = iterator.next();

			if(totalItemsShipped.containsKey(finishedGood)) {
				int newQuantity = totalItemsShipped.get(finishedGood) + salesOrderItemCount.get(finishedGood);
				totalItemsShipped.put(finishedGood, newQuantity);
			} else {
				totalItemsShipped.put(finishedGood, salesOrderItemCount.get(finishedGood));
			}

		}

	}

	public SalesOrderService getSalesOrderService() {
		return salesOrderService;
	}

	public void setSalesOrderService(SalesOrderService salesOrderService) {
		this.salesOrderService = salesOrderService;
	}

	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	public CustomerNotificationService getCustomerNotificationService() {
		return customerNotificationService;
	}

	public void setCustomerNotificationService(CustomerNotificationService customerNotificationService) {
		this.customerNotificationService = customerNotificationService;
	}
}
