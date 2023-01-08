# Java Project -- Web Inventory System
## Start From Environmental Setup
1. SpringBoot 2.6.3
2. PostgreSQL 14
3. Tomcat 9.0.56 embedded in SpringBoot
4. Eclipse 2021-12
6. Java 11

## Lombok installation in Eclipse
Lombok is a library that facilitates many tedious tasks and reduces Java source code verbosity<br>
1. Download Lombok jar file
2. Type "java -jar lombok-1.18.4.jar" in concole
3. Installation finish, then restart Eclipse
4. Project should be able to use @Data annotation now


# Step-By-Step Procedures

## Step 1: Create Spring project from Spring Intializr
Go to the [Spring Initializer](https://start.spring.io/)
- Choose "Maven Project", Language "Java" and Spring Boot version "2.6.3"
- Group: type "shop"
- Artifact: type “shopApp”
- Name: type “shopApp”
- Description: type any description
- Choose “Jar”, it will include embedded Tomcat server provided by Spring Boot
- Choose Java SDK 11

Add the following Dependencies
- Spring Web: required for RESTful web applications
- Spring Data JPA: required to access the data from the database. JPA (Java Persistence API) 
- PostgreSQL Driver: required to connect with PostgreSQL database
- Thymeleaf Driver: Thymeleaf is a Java-based library provides a good support for XHTML/HTML5 in web applications

<img width="1219" alt="Screenshot1" src="https://user-images.githubusercontent.com/48862763/151650813-c310bf0b-517a-49fc-80ff-fedfa662ed81.png">

Click the "Generate" button at the bottom of the screen, this will generate a project Zip file <br>
Then import project into Eclipse

## Step 1.1: Inject WebJars in pom.xml
WebJars are client side dependencies packaged into JAR files, add following dependency in pom.xml
1. WebJars bootstrap 5.1.3
2. WebJars jquery 3.6.0

```xml
<dependency>
	<groupId>org.webjars</groupId>
	<artifactId>bootstrap</artifactId>
	<version>5.1.3</version>
</dependency>
<dependency>
	<groupId>org.webjars</groupId>
	<artifactId>jquery</artifactId>
	<version>3.6.0</version>
</dependency>
```

## Step 2: Add sub-class to the project
 
- Repository: DAO(Data Access Object) layer which connects and accesses to the database
- Service: This layer calls the DAO and perform CRUD operations
- Model: The class mapping to the database table and provides getter and setter functions
- Controller: the class mapping to REST APIs controller for HTTP requests

### Step 2.1: Model class

```Java
package shop.shopApp.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="`inventory`")
public class shopModel {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;
	
	@Column(name="price")
    private Integer price;
    
    @Column(name="item")
    private String item;

}
```

### Step 2.2: Repository class

```Java
package shop.shopApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import shop.shopApp.model.shopModel;

@Repository
public interface shopRepository extends JpaRepository<shopModel, Long> {

	@Query(value="select * from inventory a where a.item = :item", nativeQuery=true)
    List<shopModel> getItem(String item);
}
```

### Step 2.3: Service class

```Java
package shop.shopApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.shopApp.model.shopModel;
import shop.shopApp.repository.shopRepository;

@Service
public class shopService {
	@Autowired
	shopRepository rep;
	
	public shopModel saveItem(shopModel m) {
		return rep.save(m);
	}

	public void deleteItem(Long id) {
		rep.deleteById(id);
	}
	
	public List<shopModel> listAll() {
		return rep.findAll();
	}
	
	public shopModel listOneItem(Long id) {
		return rep.findById(id).get();
	}
	
	public void updateItem(String item, Integer price) {

		List<shopModel> ll = rep.getItem(item);
		for(shopModel s:ll) {
			s.setPrice(price);
			rep.save(s);
		}
		
	}
}
```

### Step 2.4: Controller class

```Java
package shop.shopApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.shopApp.model.shopModel;
import shop.shopApp.service.shopService;

@Controller
public class shopController {
	
	@Autowired
	shopService service;
	
	@PostMapping("/add")
    public String postAdd(@ModelAttribute("shopModel") shopModel model) {
		service.saveItem(model);
		return "redirect:/index";
    }
	
	@GetMapping(value="/")
    public String GetDefault() {
        return "redirect:/index";
    }
    
    @GetMapping(value="/index")
    public String GetIndex(Model model, @ModelAttribute("shopModel") shopModel smodel) {
    	model.addAttribute("allItems", service.listAll());
        return "index";
    }

    @GetMapping(value="/showItem/{pid}")
    public String getShowItem(@PathVariable(value = "pid") Long id, Model model, @ModelAttribute("shopModel") shopModel smodel) {
    	shopModel cur = service.listOneItem(id);
    	model.addAttribute("shopModel", cur);
        return "update";
    }
    
    @PostMapping(value="/updateItem")
    public String postUpdateItem(@ModelAttribute("shopModel") shopModel smodel) {
    	service.updateItem(smodel.getItem(), smodel.getPrice());
        return "redirect:/index";
    }

    @GetMapping(value="/delete/{pid}")
    public String getDelete(@PathVariable(value = "pid") Long id) {
        service.deleteItem(id);
        return "redirect:/index";
    }
}

```

### Step 2.5: HTML files

Create "index.html" under "src/resources/templates"

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>ShopAPP</title>
    <link rel="stylesheet" href="/webjars/bootstrap/5.1.3/css/bootstrap.min.css" />
    <script src="/webjars/jquery/3.6.0/jquery.min.js"></script>
    <script src="/webjars/bootstrap/5.1.3/js/bootstrap.min.js"></script>
</head>
<body> 

<div class="container"><br/>
    <div class="alert alert-success">
        <strong>Inventory System</strong>
    </div>
</div>

<div class="container">
    <form th:action="@{/add}" method="post" th:object="${shopModel}">
                <div class="form-group">
                    <label for="name">Item</label>
                    <input type="text" class="form-control" required th:field="*{item}" 
                           placeholder="Enter Item" size="10">
                </div>
                <div class="form-group">
                    <label for="name">Price</label>
                    <input type="number" class="form-control" required th:field="*{price}"
                           placeholder="Enter Price" size="10">
                </div>
                <br>
				<button type="submit" class="btn btn-primary">Add One Item</button>
    </form>
</div>

<div class="container">
    <table class="table">
        <thead class="thead-light">
        <tr>
            <th scope="col">Item</th>
            <th scope="col">Price</th>
            <th scope="col">Delete</th>
            <th scope="col">Update</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="shopModel, iStat : ${allItems}">
            <th scope="row" th:text="${shopModel.item}">1</th>
            <td th:text="${shopModel.price}">number</td>

            <td><a href="" th:href="@{/delete/{id}(id=${shopModel.id})}" class="btn btn-danger">Delete</a></td>
            <td><a href="" th:href="@{/showItem/{id}(id=${shopModel.id})}" class="btn btn-warning">Update</a></td>
        </tr>
        </tbody>
    </table>
</div>

</body>
</html>
```

Create "update.html" under "src/resources/templates"

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>ShopAPP</title>
    <link rel="stylesheet" href="/webjars/bootstrap/5.1.3/css/bootstrap.min.css" />
    <script src="/webjars/jquery/3.6.0/jquery.min.js"></script>
    <script src="/webjars/bootstrap/5.1.3/js/bootstrap.min.js"></script>
</head>
<body> 

<div class="container"><br/>
    <div class="alert alert-success">
        <strong>Update Item's Price</strong>
    </div>
</div>

<div class="container">
    <form th:action="@{/updateItem}" method="post" th:object="${shopModel}">
                <div class="form-group">
                    <label for="name">Name</label>
                    <input class="form-control" type="text" required th:field="*{item}" placeholder="Enter Item" readonly>
                </div>
                <div class="form-group">
                    <label for="name">Price</label>
                    <input class="form-control" type="number" required th:field="*{price}" placeholder="Enter Item">
                </div>
                <br>
				<button type="submit" class="btn btn-primary">Update</button>
    </form>
</div>

</body>
</html>
```

## Step 3: Install PostgreSQL Database
 
 [PostgreSQL Download](https://www.postgresql.org/) and installation <br>
 Create user account <br>
 Create database, name: postgres <br>
 Connection configuration: host:localhost, port:1234 <br>
 
## Step 4: Build Application

To connect PostgreSQL, type database details in "application.properties" under "src/main/resources" as following

```Java
# Postgres database, account
spring.datasource.url = jdbc:postgresql://localhost:1234/postgres
spring.datasource.username  = postgres
spring.datasource.password  = 1234
server.port=8080

#spring.datasource.driver-class-name=org.postgresql.Driver
# Keep the connection alive if idle for a long time (needed in production)
spring.datasource.testWhileIdle=true
spring.datasource.validationQuery=SELECT 1
# ===============================
# = JPA / HIBERNATE
# ===============================
# Show or not log for each sql query
spring.jpa.show-sql=true
# Hibernate ddl auto (create, create-drop, update): with "create-drop" the database
# schema will be automatically created afresh for every start of application
spring.jpa.hibernate.ddl-auto=create-drop

# Naming strategy
#spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl
#spring.jpa.hibernate.naming.physical-strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy

# Allows Hibernate to generate SQL optimized for a particular DBMS
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```
Right click project on Eclipse and select "Run As" then choose "4 Maven build"<br>
In the "main" tab, type "spring-boot:run" in "Goals"<br>
In the "JRE" tab, type "-Dfork=false" in "VM Arguments". So, we can stop Tomcat in Eclipse<br>
Click on "Apply" then "Run"<br>

## Step 5: Test

### Start from http:localhost:8080 or http:localhost:8080/index
- Has @GetMapping(value="/") redirect to "redirect:/index" in ShopControll class, so both link are actually point to @GetMapping(value="/index")
- In @GetMapping(value="/index"), use "model.addAttribute" to bind data to view(html)
- Screenshot, there is no items when first time login

<img width="1374" alt="Screenshot3" src="https://user-images.githubusercontent.com/48862763/151665774-e0141d40-2adc-4de5-aa83-fea6c73cc15d.png">

- In index.html display all existing items by Thymeleaf expression
```html
<tr th:each="shopModel, iStat : ${allItems}">
    <th scope="row" th:text="${shopModel.item}">1</th>
    <td th:text="${shopModel.price}">number</td>

    <td><a href="" th:href="@{/delete/{id}(id=${shopModel.id})}" class="btn btn-danger">Delete</a></td>
    <td><a href="" th:href="@{/showItem/{id}(id=${shopModel.id})}" class="btn btn-warning">Update</a></td>
</tr>
```
	
### Add the first and second items
- Click on "Add One Item" button, action was handeled by 
```html
<form th:action="@{/add}" method="post" th:object="${shopModel}">
```
- @PostMapping("/add") will process info and save it in the database and redirect to index page
- Screenshot, 2 items saved

<img width="1215" alt="Screenshot5" src="https://user-images.githubusercontent.com/48862763/151666481-80fc1ae6-1e88-4a85-b8c2-85618d93b4f3.png">

### Update the second item	
- Click on "Update" button of second item, action was handeled by
```html
<a href="" th:href="@{/showItem/{id}(id=${shopModel.id})}" class="btn btn-warning">Update</a>
```
- @GetMapping(value="/showItem/{pid}") in ShopController class will handle the request
- "model.addAttribute("shopModel", cur)" to bind data to view(html)
- In "update.html", data will be handled by
```html
<form th:action="@{/updateItem}" method="post" th:object="${shopModel}">
```
- @PostMapping(value="/updateItem") will process data update
- ModelAttribute("shopModel") shopModel smodel is the data object from html 

<img width="1056" alt="Screenshot6" src="https://user-images.githubusercontent.com/48862763/151676879-72f67e1a-2cef-44e1-8f2e-24b1aac98e20.png">

### Delete item 

- Click on "Delete" button, action was handeled by
```html
<a href="" th:href="@{/delete/{id}(id=${shopModel.id})}" class="btn btn-danger">Delete</a>
```
- In controller, action was handled by @GetMapping(value="/delete/{pid}"

<img width="1218" alt="Screenshot7" src="https://user-images.githubusercontent.com/48862763/151677021-7bbd1bf6-fc5d-47c2-be48-a65bb4576aea.png">























