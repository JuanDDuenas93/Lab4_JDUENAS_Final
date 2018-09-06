
var url = 'localhost:8080';

function getOrders() {
    return axios.get(url+"/Orders").then(function (response) {
    return response.data;
  })
}

function getProducts() {
    return axios.get(url+"/Orders/products").then(function (response) {
    return response.data;
  })
}

function clearTables(){
    var tables = document.getElementsByClassName("table"); 
    for (i = 0; i < tables.length; i++) {
        tables[i].innerHTML = "";
    }
}

function addOrder(order) {
    var tableOrder = document.getElementById("tableOrder");
    var header = document.createElement("tr");
    var cell = document.createElement("th");
    cell.innerHTML = "Product";
    header.appendChild(cell);
    var cell = document.createElement("th");
    cell.innerHTML = "Quantity";
    header.appendChild(cell);
    var cell = document.createElement("th");
    cell.innerHTML = "Price";
    header.appendChild(cell);
    tableOrder.appendChild(header);
    tableOrder.setAttribute("class","table");
    for (var i = 0; i < order.products.length; i++) {
        var row = document.createElement("tr");

        var cell = document.createElement("td");
        cell.innerHTML = order.products[i].product;
        row.appendChild(cell);

        var cell = document.createElement("td");
        cell.innerHTML = order.products[i].quantity;
        row.appendChild(cell);

        var cell = document.createElement("td");
        cell.innerHTML = order.products[i].price;
        row.appendChild(cell);

        tableOrder.appendChild(row);
    }
}

function loadOrders(){
    //clearTables();
    loadData(orderBuilder);
}

function loadData(callback){
    axios.all([getOrders(),getProducts()])
            .then(axios.spread(function (orders, products){
                callback(orders,products);
        }));
}

function orderBuilder(orders ,products){
    for(i in orders){
        var orderJson = orders[i];
        var order = {order_id:orderJson.tableNumber, table_id:orderJson.tableNumber, products:[]};
        for(productName in orderJson.orderAmountsMap){
            var prod = {"product": productName ,"quantity": orderJson.orderAmountsMap[productName],"price": null}
            prod.price = products.filter(function (x){
                return x.name === productName;
            })[0].price;
            order.products.push(prod);
        }
        addOrder(order);
    }
}