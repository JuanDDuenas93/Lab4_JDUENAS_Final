/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/Orders")
public class OrdersAPIController {

    @Autowired
    RestaurantOrderServices OrderServices;
    
    //Get all the orders available
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getOrdersHandler() {
        try {
            Set<Integer> Orders = OrderServices.getTablesWithOrders();
            List<Order> Ord = new LinkedList<>();
            for (Integer i : Orders) {
                Ord.add(OrderServices.getTableOrder(i));
            }

            System.out.println(Orders);
            return new ResponseEntity<>(Orders, HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        }
    }
    
    //Get a table by ID
    @RequestMapping(path = "/{tableID}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrdersByIdHandler(@PathVariable int tableID) {
        try {
            return new ResponseEntity<>(OrderServices.getTableOrder(tableID), HttpStatus.ACCEPTED);
             
        } catch (OrderServicesException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Sorry, we couldn't find a table with that ID", HttpStatus.NOT_FOUND);
        }
    }
    
    //Create a new order
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> postNewOrderHandler(@RequestBody Order newOrder) {
        try {
            OrderServices.addNewOrderToTable(newOrder);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        }

    }
    
    @RequestMapping(path = "/{tableID}/total", method = RequestMethod.GET)
    public ResponseEntity<?> getTableTotalHandler(@PathVariable int tableID) {

        try {
            return new ResponseEntity<>(OrderServices.calculateTableBill(tableID), HttpStatus.ACCEPTED);

        } catch (Exception ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);

        }

    }
    
    @RequestMapping(path = "/{tableID}/NewProduct", method = RequestMethod.PUT)
    public ResponseEntity<?> putNewProductHandler(@PathVariable int tableID, @RequestBody String newProduct) {
        try {
            OrderServices.addProductToOrder(tableID,newProduct);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        }

    }
    
    @RequestMapping(path = "/{tableID}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteOrderHandler(@PathVariable Integer tableID) {
        try {
            OrderServices.deleteOrder(tableID);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        }

    }
    
    @RequestMapping("/products")
    public ResponseEntity<?> getProducts(){
        HttpStatus status = HttpStatus.ACCEPTED;
        Collection<RestaurantProduct> entry = null;
        try {
            entry = OrderServices.getProducts();
        } catch (OrderServicesException ex) {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(entry,HttpStatus.ACCEPTED);
    }
    
}
