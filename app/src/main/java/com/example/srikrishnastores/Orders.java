package com.example.srikrishnastores;

public class Orders{
    private String orderId;
    public Orders()
    {}
    public Orders(String id){
        this.orderId=id;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


}
