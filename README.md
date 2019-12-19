# Shop REST webservice

### How to run application
1. Download application
2. Open Command Prompt and enter folder containing pom.xml
2. Run command 'mvn spring-boot:run'

### API documentation

#####1. Add new product
  * URL: localhost:8080/item
  * Method: POST
  * URL Params: none
  * Data Params:
  <br /> { name : string, price : string }
  * Success Response: 
  <br /> CODE: 200 
  <br /> { name : string, price : string }
  
#####2. Retrieve list of products
  * URL: localhost:8080/items
  * Method: GET
  * URL Params: none
  * Data Params: none
  * Success Response: 
  <br /> CODE: 200 
  <br /> {[{ name : string, price : string },{ name : string, price : string }]}

#####3. Place an order
  * URL: localhost:8080/order
  * Method: POST
  * URL Params: none
  * Data Params:
  <br /> {"purchasedProducts":[{"productId":1,"quantity":1},{"productId":2,"quantity":2}]}
  * Success Response: 
  <br /> CODE: 200 
  <br /> {"purchasedProducts":[{"productId":1,"quantity":2},{"productId":2,"quantity":1}],"orderNumber":1,"totalValue":10,"orderDate":null}
  
#####4. Update product
  * URL: localhost:8080/order/{id}
  * Method: PUT
  * URL Params: id
  * Data Params:
  <br /> {"name":"string"","price":1.1}
  * Success Response: 
  <br /> CODE: 200 
  <br /> {"name":"string"","price":1.1}
  
#####5. Recalculate total value of order
  * URL: localhost:8080/order/{id}
  * Method: POST
  * URL Params: id
  * Data Params: none
  * Success Response: 
  <br /> CODE: 200 
  <br /> {"purchasedProducts":[{"productId":1,"quantity":1},{"productId":2,"quantity":2}],"orderNumber":1, "totalValue":10, "orderDate":"yyyy-MM-dd"}

#####6. Recalculate total value of order
  * URL: localhost:8080/orders/{startdate}/{enddate}
  * Method: GET
  * URL Params: "yyyy-MM-dd" startDate, "yyyy-MM-dd" enddate
  * Data Params: none
  * Success Response: 
  <br /> CODE: 200 
  <br /> [{"purchasedProducts":[{"productId":1,"quantity":1},{"productId":2,"quantity":2}],"orderNumber":1, "totalValue":10, "orderDate":"yyyy-MM-dd"}]