package com.example.depu.model;

public class ExchangeItem {
    /**
     * 兑换物品唯一标识符
     */
    private String id;
    
    /**
     * 兑换物品名称
     */
    private String name;
    
    /**
     * 兑换所需积分
     */
    private int points;
    
    /**
     * 库存数量
     */
    private int stock;
    
    /**
     * 物品类型
     */
    private String type;

    public ExchangeItem(String id, String name, int points, int stock, String type) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.stock = stock;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
