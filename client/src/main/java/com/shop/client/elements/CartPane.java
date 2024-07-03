package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.client.elements.views.CartProductView;
import com.shop.client.elements.views.ProductView;
import com.shop.common.model.Product;

public class CartPane extends ProductMenu {

    public CartPane(Starter starter) {
        super(starter);
    }

    @Override
    protected ProductView getProductView(Starter starter, Product product) {
        return new CartProductView(starter, product);
    }
}
