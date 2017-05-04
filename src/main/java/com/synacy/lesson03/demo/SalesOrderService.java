package com.synacy.lesson03.demo;

import com.synacy.lesson03.demo.domain.DataAccessException;
import com.synacy.lesson03.demo.domain.SalesOrder;
import com.synacy.lesson03.demo.domain.SalesOrderStatus;
import com.synacy.lesson03.demo.domain.ShippingBatch;

import java.util.List;

public interface SalesOrderService {

	List<SalesOrder> fetchSalesOrderDueForShipment(ShippingBatch shippingBatch)
			throws DataAccessException;

	void updateStatus(SalesOrder salesOrder, SalesOrderStatus shipped);

}
