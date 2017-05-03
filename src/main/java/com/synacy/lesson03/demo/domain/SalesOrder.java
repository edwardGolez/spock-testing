package com.synacy.lesson03.demo.domain;

import java.util.Map;

public interface SalesOrder {
	Map<FinishedGood,Integer> getSalesOrderItemCount();
}
