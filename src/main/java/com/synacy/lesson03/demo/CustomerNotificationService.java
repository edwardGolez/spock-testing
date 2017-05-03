package com.synacy.lesson03.demo;

import com.synacy.lesson03.demo.domain.NotificationType;
import com.synacy.lesson03.demo.domain.SalesOrder;

public interface CustomerNotificationService {
	void notifyCustomer(NotificationType salesOrderStatus, SalesOrder salesOrder);
}
