package com.shop.client.elements.views;

import com.shop.client.Starter;
import com.shop.common.model.Product;

public class CartProductView extends ProductView{
    public CartProductView(Starter starter, Product product) {
        super(starter, product);
        remove.setOnAction(e -> removeProduct(product.getId(), 0));
    }

    @Override
    protected void removeProduct(int id, int amount) {
        starter.getController().removeCartProduct(id);
    }
}
