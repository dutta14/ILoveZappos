package anindya.ilovezappos;

import java.util.ArrayList;
import java.util.HashMap;

import anindya.ilovezappos.model.Result;

/**
 * Created by anind on 5/14/2017.
 */

public class Cart {

    public static HashMap<String, Result> cart;

    public static void init() {
        cart = new HashMap<>();
    }

    public static boolean update(Result item) {
        if(cart.containsKey(item.getProductId())) {
            cart.remove(item.getProductId());
            return false;
        }
        cart.put(item.getProductId(), item);
        return true;
    }

    public static boolean has(Result item) {
        return cart.containsKey(item.getProductId());
    }

    public static Result[] getItems() {
        ArrayList<Result> items = new ArrayList<>();
        for(String key: cart.keySet()) {
            items.add(cart.get(key));
        }

        return items.toArray(new Result[items.size()]);
    }

}
