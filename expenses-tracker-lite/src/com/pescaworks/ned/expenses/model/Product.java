package com.pescaworks.ned.expenses.model;

public class Product {
	String category;
	String subcategory;
	String item;
	int price;
	int cost;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		String string = "[Product:Category:" + category +
						";SubCategory:" + subcategory +
						";Item:" + item +
						";Price:" + price +
						";Cost:" + cost +"]";
		return string;
	}
	
	
}
