package com.shop.client.elements.views;

import com.shop.client.Starter;
import com.shop.common.model.Product;

public class CrProductView extends ProductView{
    public CrProductView(Starter starter, Product product) {
        super(starter, product);
    }

    @Override
    protected void removeProduct(int id, int amount) {
        starter.getController().removeCreatedProduct(id, amount);
    }
}
