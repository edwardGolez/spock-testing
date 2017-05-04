package com.synacy.lesson03.demo

import com.synacy.lesson03.demo.domain.BatchShippingException
import com.synacy.lesson03.demo.domain.DataAccessException
import com.synacy.lesson03.demo.domain.FinishedGood
import com.synacy.lesson03.demo.domain.SalesOrder
import com.synacy.lesson03.demo.domain.SalesOrderStatus
import com.synacy.lesson03.demo.domain.ShippingBatch
import spock.lang.Specification

class ShippingServiceSpec extends Specification {

	ShippingService service

	SalesOrderService salesOrderService
	WarehouseService warehouseService
	CustomerNotificationService customerNotificationService
	SystemAlarmService systemAlarmService

	void setup() {
		service = new ShippingService()

		salesOrderService = Mock()
		warehouseService = Mock()
		customerNotificationService = Mock()
		systemAlarmService = Mock()

		service.salesOrderService = salesOrderService
		service.warehouseService = warehouseService
		service.customerNotificationService = customerNotificationService
		service.systemAlarmService = systemAlarmService
	}

	def "shipSalesOrderByBatch should update each sales order for shipment to SHIPPED"() {
		given:
		ShippingBatch shippingBatch = Mock()

		SalesOrder salesOrder1 = Mock()
		salesOrder1.getSalesOrderItemCount() >> [:]

		SalesOrder salesOrder2 = Mock()
		salesOrder2.salesOrderItemCount >> [:]

		def salesOrders = [
			salesOrder1, salesOrder2
		]
		salesOrderService.fetchSalesOrderDueForShipment(shippingBatch) >> salesOrders

		when:
		service.shipSalesOrderByBatch(shippingBatch)

		then:
		1 * salesOrderService.updateStatus(salesOrder1, SalesOrderStatus.SHIPPED)
		1 * salesOrderService.updateStatus(salesOrder2, SalesOrderStatus.SHIPPED)
	}

	def "shipSalesOrderByBatch should alarm the system if a data access exception is thrown when fetching sales order for shipment" () {
		given:
		ShippingBatch shippingBatch = Mock()

		def dataAccessException = new DataAccessException()
		salesOrderService.fetchSalesOrderDueForShipment(shippingBatch) >> { sb ->
			throw dataAccessException
		}

		when:
		service.shipSalesOrderByBatch(shippingBatch)

		then:
		1 * systemAlarmService.alarm(dataAccessException)
		thrown(Exception)
	}

	def """shipSalesOrderByBatch should throw batch shipping exception containing cause if a
	data access exception is thrown fetching of sales order for shipment is thrown"""() {
		given:
		ShippingBatch shippingBatch = Mock()

		def dataAccessException = new DataAccessException()
		salesOrderService.fetchSalesOrderDueForShipment(shippingBatch) >> { sb ->
			throw dataAccessException
		}

		when:
		service.shipSalesOrderByBatch(shippingBatch)

		then:
		BatchShippingException exception = thrown()
		dataAccessException == exception.cause
	}

	def "shipSalesOrderByBatch should update the inventory levels in the warehouse with the sum of all items shipped"() {
		given:
		ShippingBatch shippingBatch = Mock()

		FinishedGood finishedGood1 = Mock()
		FinishedGood finishedGood2 = Mock()
		FinishedGood finishedGood3 = Mock()

		SalesOrder salesOrder1 = Mock()
		salesOrder1.getSalesOrderItemCount() >> [
			(finishedGood1): 50,
			(finishedGood2): 30
		]

		SalesOrder salesOrder2 = Mock()
		salesOrder2.salesOrderItemCount >> [
			(finishedGood2): 10,
			(finishedGood3): 7
		]

		def salesOrders = [
			salesOrder1, salesOrder2
		]
		salesOrderService.fetchSalesOrderDueForShipment(shippingBatch) >> salesOrders

		Map expectedTotalItemsShipped = [
			(finishedGood1): 50,
			(finishedGood2): 40,
			(finishedGood3): 7
		]

		when:
		service.shipSalesOrderByBatch(shippingBatch)

		then:
		1 * warehouseService.updateInventoryLevel(expectedTotalItemsShipped)
	}


}
