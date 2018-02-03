package com.pescaworks.ned.expenses.model;

public class TableEntry {
	private String item;
	private String vendor;
	private double unitPrice;
	private int quantity;
	private double totalPrice;
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	@Override
	public String toString() {
		String string = "[TableEntry:Item:" + item +
						";UnitPrice:" + unitPrice +
						";Quantity:" + quantity +
						";TotalPrice:" + totalPrice + "]";
		return string;
	}
}
