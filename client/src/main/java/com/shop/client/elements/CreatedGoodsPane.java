package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.client.elements.views.CrProductView;
import com.shop.client.elements.views.ProductView;
import com.shop.common.model.Product;

public class CreatedGoodsPane extends ProductMenu {
    public CreatedGoodsPane(Starter starter) {
        super(starter);
    }

    @Override
    protected ProductView getProductView(Starter starter, Product product) {
        return new CrProductView(starter, product);
    }
}
