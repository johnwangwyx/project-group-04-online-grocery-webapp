package ca.mcgill.ecse321.project321.controller;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.project321.Project321BackendApplication;
import ca.mcgill.ecse321.project321.dto.AddressDTO;
import ca.mcgill.ecse321.project321.dto.CartDTO;
import ca.mcgill.ecse321.project321.dto.CartItemDTO;
import ca.mcgill.ecse321.project321.dto.CustomerDTO;
import ca.mcgill.ecse321.project321.dto.EmployeeDTO;
import ca.mcgill.ecse321.project321.dto.InStoreBillDTO;
import ca.mcgill.ecse321.project321.dto.EmployeeDTO.EmployeeStatusDTO;
import ca.mcgill.ecse321.project321.dto.InStoreBillDTO;
import ca.mcgill.ecse321.project321.dto.OrderDTO;
import ca.mcgill.ecse321.project321.dto.ProductDTO;
import ca.mcgill.ecse321.project321.dto.TimeSlotDTO;
import ca.mcgill.ecse321.project321.dto.UserDTO;
import ca.mcgill.ecse321.project321.dto.CartDTO.ShoppingTypeDTO;
import ca.mcgill.ecse321.project321.dto.ProductDTO.PriceTypeDTO;
import ca.mcgill.ecse321.project321.dto.ShiftDTO;
import ca.mcgill.ecse321.project321.dto.StoreDTO;
import ca.mcgill.ecse321.project321.dto.StoreOwnerDTO;
import ca.mcgill.ecse321.project321.model.Address;
import ca.mcgill.ecse321.project321.model.Cart;
import ca.mcgill.ecse321.project321.model.CartItem;
import ca.mcgill.ecse321.project321.model.Customer;
import ca.mcgill.ecse321.project321.model.Employee;
import ca.mcgill.ecse321.project321.model.InStoreBill;
import ca.mcgill.ecse321.project321.model.Employee.EmployeeStatus;
import ca.mcgill.ecse321.project321.model.InStoreBill;
import ca.mcgill.ecse321.project321.model.Order;
import ca.mcgill.ecse321.project321.model.Product;
import ca.mcgill.ecse321.project321.model.TimeSlot;
import ca.mcgill.ecse321.project321.model.Cart.ShoppingType;
import ca.mcgill.ecse321.project321.model.Product.PriceType;
import ca.mcgill.ecse321.project321.model.Shift;
import ca.mcgill.ecse321.project321.model.Store;
import ca.mcgill.ecse321.project321.model.StoreOwner;
import ca.mcgill.ecse321.project321.service.GroceryStoreService;

@CrossOrigin(origins = "*")
@RestController
public class GroceryStoreController {
    
    @Autowired
    private GroceryStoreService service;

    @GetMapping(value = {"/customers", "/customers/"})
    public List<CustomerDTO> getAllCustomers() throws IllegalArgumentException{
        return convertCustomerListToDTO(service.getAllCustomers());
    }

    @GetMapping(value = {"/customers/{email}", "/customers/{email}/"})
    public CustomerDTO getCustomer(@PathVariable("email") String email) throws IllegalArgumentException{
        return convertToDTO(service.getCustomer(email));
    }

    @PostMapping(value = {"/customers", "/customers/"})
    public CustomerDTO createCustomer(@RequestParam(name = "email")     String email, 
                                      @RequestParam(name = "name")      String name, 
                                      @RequestParam(name = "password")  String password, 
                                      @RequestParam(name = "phone")     String phone, 
                                      @RequestParam(name = "address")   AddressDTO address) 
    throws IllegalArgumentException {
        Customer c = service.createCustomer(email, name, password, phone, convertToDomainObject(address));
        //System.out.println(c.getName());
        return convertToDTO(c);
    }
    
    @GetMapping(value = {"/storeOwners", "/storeOwners/"})
    public StoreOwnerDTO getStoreOwner() throws IllegalArgumentException{
        return convertToDTO(service.getStoreOwner());
    }
    
    @PostMapping(value = {"/storeOwners", "/storeOwners/"})
    public StoreOwnerDTO createStoreOwner(@RequestParam(name = "email")     String email, 
                                          @RequestParam(name = "name")      String name, 
                                          @RequestParam(name = "password")  String password) 
    throws IllegalArgumentException, IllegalAccessException {
        if(service.getStoreOwner() != null) {
            throw new IllegalAccessException("A store owner already exits! Cannot create another");
        }
        StoreOwner c = service.createStoreOwner(email, name, password);
        System.out.println(c.getName());
        return convertToDTO(c);
    }

    @PostMapping(value = {"/storeOwners/info", "/storeOwners/info/"})
    public StoreOwnerDTO setStoreOwnerInfo(@RequestParam(name = "email")     String email, 
                                           @RequestParam(name = "name")      String name, 
                                           @RequestParam(name = "password")  String password){
        StoreOwner c = service.setStoreOwnerInfo(email, name, password);
        return convertToDTO(c);
    }

    @GetMapping(value = {"/employee/{email}", "/employee/{email}/"})
    public EmployeeDTO getEmployee(@PathVariable("email") String email) throws IllegalArgumentException{
        return convertToDTO(service.getEmployee(email));
    }
    
    @PostMapping(value = {"/login", "/login/"})
    public UserDTO login(@RequestParam(name = "email")     String email, 
                         @RequestParam(name = "password")  String password, 
                         @RequestParam(name = "userType")  String userType)
    throws IllegalArgumentException {
    	switch (userType.toLowerCase()) {
		case "owner":
		case "storeowner":
			StoreOwnerDTO so =convertToDTO(service.getStoreOwner());
			if (so.getPassword().equals(password)) {
				Project321BackendApplication.setCurrentUser(so);
				Project321BackendApplication.setUserType("owner");
				return so;
			}
			else {
				throw new IllegalArgumentException("password do not match");
			}
			
		case "customer":
			CustomerDTO c =convertToDTO(service.getCustomer(email));
			if (c != null) {
				if (c.getPassword().equals(password)) {
					Project321BackendApplication.setCurrentUser(c);
					Project321BackendApplication.setUserType("customer");
					return c;
				}
				else {
					throw new IllegalArgumentException("password do not match");
				}
			}
			break;
			
		case "employee":
			EmployeeDTO e =convertToDTO(service.getEmployee(email));
			if (e != null) {
				if (e.getPassword().equals(password)) {
					Project321BackendApplication.setCurrentUser(e);
					Project321BackendApplication.setUserType("employee");
					return e;
				}
				else {
					throw new IllegalArgumentException("password do not match");
				}
			}
			break;
		default:
			break;
		}
    	return null;
    }
    
    @GetMapping(value = {"/carts", "/carts/"})
    public List<CartDTO> getAllCarts() throws IllegalArgumentException {
        return convertCartListToDTO(service.getAllCarts());
    }

    @PostMapping(value = {"/carts", "/carts/"})
    public CartDTO createCart(@RequestParam(name = "type")      ShoppingTypeDTO type,
                              @RequestParam(name = "customeremail")  String customerEmail) {

        if(Project321BackendApplication.getUserType() != null && Project321BackendApplication.getUserType().contentEquals("customer")) {
            Date creationDate = java.sql.Date.valueOf(LocalDate.now());
            Time creationTime = java.sql.Time.valueOf(LocalTime.now());
            Customer customer = service.getCustomer(customerEmail);
            Cart c = service.createCart(translateEnum(type), customer, creationDate, creationTime);
            CartDTO cart = convertToDTO(c);
            Project321BackendApplication.setCart(cart); // Set current cart in session
            return cart;
        }
        else {
            throw new IllegalArgumentException("Must be logged in as a customer.");
        }
    }

    /**
     * This implements part of Req. 06 which relates to setting a desired time slot
     * The Grocery software system shall only allow delivery and pick-up time slots to be reserved by customers if there is one 
     * employee available to tend to the delivery or pick-up during that time slot
     * @return returns list of timeslots that are available
     * @throws IllegalArgumentException
     */
    @PostMapping(value = {"/carts/timeslot", "/carts/timeslot/"})
    public CartDTO setCartTimeSlot(@RequestParam(name = "cartcustomeremail") String customerEmail,
                                   @RequestParam(name = "cartdate") @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date cartDate,
                                   @RequestParam(name = "carttime") @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date cartTime, 
                                   @RequestParam(name = "timeslotdate") @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date timeSlotDate,
                                   @RequestParam(name = "timeslotstarttime") @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date timeSlotStartTime,
                                   @RequestParam(name = "timeslotendtime") @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date timeSlotEndTime) {
        Customer customer = service.getCustomer(customerEmail);
        TimeSlot timeSlot = service.getTimeSlot(new Date(timeSlotDate.getTime()), new Time(timeSlotStartTime.getTime()), new Time(timeSlotEndTime.getTime()));
        Cart cart = service.getCartByCustomerAndDateAndTime(customer, new Date(cartDate.getTime()), new Time(cartTime.getTime()));
        cart = service.setTimeSlot(cart, timeSlot);
        return convertToDTO(cart);
    }
    
    /**
     * This implements part of Req. 06 which relates to requesting to see all available timeslots
     * The Grocery software system shall only allow delivery and pick-up time slots to be reserved by customers if there is one 
     * employee available to tend to the delivery or pick-up during that time slot
     * @return returns list of timeslots that are available
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/availabletimeslots", "/availabletimeslots/"})
    public List<TimeSlotDTO> availableTimeSlots() throws IllegalArgumentException {
    	List<TimeSlot> tslot = service.getAllTimeSlots();
    	if(tslot == null) {
    		throw new IllegalArgumentException("No TimeSlot exists!");
    	}
    	List<Shift> shifts = service.getAllShifts();
    	if(shifts == null) {
    		throw new IllegalArgumentException("No one on shift!");
    	}
    	List<TimeSlot> availableTimeSlots = new ArrayList<>();
    	for(TimeSlot t: tslot) {
            if(t.getMaxOrderPerSlot() > 0) {
                availableTimeSlots.add(t);
            }
    	}
    	
    	return convertTimeSlotListToDTO(availableTimeSlots);
    }

    /**
     * This implements Req. 09
     * The Grocery software system shall keep track of all the employees who work and have worked at the grocery store with employee accounts
     * @return returns list of empolyee, current and old
     * @throws IllegalArgumentException if userType is not owner
     */
    @GetMapping(value = {"/listemployees", "/listemployees/"})
    public List<EmployeeDTO> listEmployees() throws IllegalArgumentException{
    	if(Project321BackendApplication.getUserType() != null && Project321BackendApplication.getUserType().equals("owner")) {
            return convertEmployeeListToDTO(service.getAllEmployee());    		
    	}
    	throw new IllegalArgumentException("Only owner can do this!");
    }
    
    /**
     * This implements Req. 10
     * The Grocery Store System shall allow the owner to remove or add employees on the employment list.
     * Deleting employ does not return anything. Just outputs string "Employee deleted" upon success.
     * @return returns the newly added employee
     * @throws IllegalArgumentException if userType is not owner
     */
    @PostMapping(value = {"/employee", "/employee/"})
    public EmployeeDTO owneraddEmployee(@RequestParam(name = "email")     String email, 
            @RequestParam(name = "name")      String name, 
            @RequestParam(name = "password")  String password,
            @RequestParam(name = "status")     EmployeeStatusDTO status) throws IllegalArgumentException {
    	if(Project321BackendApplication.getUserType() != null && Project321BackendApplication.getUserType().equals("owner")) {
    		Employee e = service.createEmployee(email, name, password, translateEnum(status));
        	return convertToDTO(e);
    	}
    	else {
    		throw new IllegalArgumentException("Only owner can do this!");
    	}
    }
    @PostMapping(value = {"/removeemployee", "/removeemployee/"})
    public void ownerremoveEmployee(@RequestParam(name = "email")     String email) {
    	if(Project321BackendApplication.getUserType() != null && Project321BackendApplication.getUserType().equals("owner")) {
    		Employee e = service.getEmployee(email);
    		service.removeEmployee(e);
    		System.out.println("Employee deleted");
    	}
    	else {
    		throw new IllegalArgumentException("Only owner can do this!");
    	}
    }
    
    ///Helper to check all the employees
    @GetMapping(value = {"/employees", "/employees/"})
    public List<EmployeeDTO> getAllEmployee() throws IllegalArgumentException{
        return convertEmployeeListToDTO(service.getAllEmployee());
    }
    
    /**
     * This implements Req. 11
     * The Grocery Store System shall allow the employee or customer to create a 
     * customer account with the customer email and physical address.
     * All this method does is check if it is the employee making the customer account. We assume when a customer is making
     * an account the userType will not be set
     * @return returns the newly added customer
     * @throws IllegalArgumentException
     */
    @PostMapping(value = {"/helpcustomer", "/helpcustomer/"})
    public CustomerDTO helpCreateCustomer(@RequestParam(name = "email")     String email, 
                                      @RequestParam(name = "name")      String name, 
                                      @RequestParam(name = "password")  String password, 
                                      @RequestParam(name = "phone")     String phone, 
                                      @RequestParam(name = "address")   AddressDTO address) 
    throws IllegalArgumentException {
    	if(!Project321BackendApplication.getUserType().equalsIgnoreCase("owner")) {   //anyone but the owner can create a customer account for customer
    		Customer c = service.createCustomer(email, name, password, phone, convertToDomainObject(address));
            return convertToDTO(c);
    	}
    	else {
    		throw new IllegalArgumentException("Only a customer or and employee can do this.");
    	}
    }
    
    /**
     * This implements Req. 12
     * The Grocery Store System shall give the customer with a local address free shipping on online 
     * delivery orders and charge an extra fee for customers outside town limits
     * @return returns true if free-shipping requirements are met, false otherwise.
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/shipping", "/shipping/"})
    public boolean shippingFeeChecker() throws IllegalArgumentException{
    	boolean freeShipping = false;
    	if(Project321BackendApplication.getUserType() != null && Project321BackendApplication.getUserType().contentEquals("customer")) {
    		UserDTO currentUser = Project321BackendApplication.getCurrentUser();
        	String email = currentUser.getEmail();
        	Customer customer = service.getCustomer(email);
        	Address customeraddress = customer.getAddress();
        	if(customeraddress.getTown().equals(Project321BackendApplication.getStore().getAddress().getTown())) {
        		freeShipping = true;
        	}
        	return freeShipping;
    	}
    	else {
    		throw new IllegalArgumentException("Must be logged in as a customer.");
    	}
    }
    ////helper method for testing things requiring customer
    @PostMapping(value = {"/testcustomer", "/testcustomer/"})
    public CustomerDTO createTestCustomer() {
    	Address custAddress = new Address("Montreal", "McgillStreet", "HHHH", 1234);
    	Customer c = service.createCustomer("bbbb", "Thomas", "789", "5149997777",custAddress);
        return convertToDTO(c);
    }
    
    /**
     * This is an implementation for Req.04
     * Req.04-As a user of the Grocery software system with a customer account, I would like to browse all products 
     * available in-stock and identify their availability for pick-up or delivery and their price
     * @return all product if user is owner or employee OR all product with stock greater than one if user is a customer
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/products", "/products/"})
    public List<ProductDTO> getAllProduct() throws IllegalArgumentException {
    	if ("customer".equals(Project321BackendApplication.getUserType())) {
    		return convertProductListDTO(service.getProductByStockGreaterThan(0));
    	}
        return convertProductListDTO(service.getAllProduct());
    }
    
    
    /**
     * This is an partial implementation of Req.13(1/3), covering the "create" aspect of the Req.
     * Req.13-The Grocery Store System shall allow the owner to add or remove items from 
     * the store inventory and set the number of that item in stock
     * @return the created product
     * @throws IllegalArgumentException
     */
    @PostMapping(value = {"/products", "/products/"})
    public ProductDTO createProduct(@RequestParam(name = "type")   PriceTypeDTO type,
                              @RequestParam(name = "productName")  String productName,
                              @RequestParam(name = "Online")  String isAviliableOnline,
    						  @RequestParam(name = "price")  int price,
    					      @RequestParam(name = "stock")  int stock) throws IllegalArgumentException{
    	if (!"owner".equals(Project321BackendApplication.getUserType())) {
    		throw new IllegalArgumentException("only owner is able to create products.");
    	}
    	Product p = service.createProduct(translateEnum(type), productName, isAviliableOnline, price, stock);
        return convertToDTO(p);
    }
    
    /**
     * This is an partial implementation of Req.13(2/3), Covering the "delete" aspect of the Req.
     * @return the deleted product
     * @throws IllegalArgumentException
     */
    @PostMapping(value = {"/products/delete", "/products/delete/"})
    public ProductDTO deleteProduct(@RequestParam(name = "productName") String productName) {
    	if (!"owner".equals(Project321BackendApplication.getUserType())) {
    		throw new IllegalArgumentException("only owner is able to delete products.");
    	}
    	Product p = service.deleteProduct(productName);
        return convertToDTO(p);
    }
    
    /**
     * This is an partial implementation of Req.13(3/3), Covering the "set stock" aspect of the Req.
     * @return the changed product
     * @throws IllegalArgumentException
     */
    @PostMapping(value = {"/products/changestock", "/products/changestock/"})
    public ProductDTO changeProductStock(@RequestParam(name = "productName") String productName,
    									 @RequestParam(name = "stock") int stock){
    	if (stock < 0) {
    		throw new IllegalArgumentException("stock cannot be a negative value");
    	}
    	if (!("owner".equals(Project321BackendApplication.getUserType()) || 
    	    "employee".equals(Project321BackendApplication.getUserType()))) {
    		throw new IllegalArgumentException("only owner or employee is able to change product stock.");
    	}
    	Product p = service.getProductByName(productName);
    	if (p == null) {
    		throw new IllegalArgumentException("the product do not exsist");
    	}
    	service.deleteProduct(productName);
    	Product newProduct = service.createProduct(p.getPriceType(), p.getProductName(), p.getIsAvailableOnline(), p.getPrice(),stock);
        return convertToDTO(newProduct);
    }
    
    /**
     * This is an implementation of the Req.15. The owner is able to set onlineAvailability upon the creation of the product, 
     * but if the owner wants to change the availability, he/she is able to do so via this method.
     * Req.15-The Grocery Store System shall allow the owner to choose what items are available for delivery and pickup online.
     * Note the isAviliableOnline is supposed to be a boolean but it is too risky to change this field for the entire project structure 
     * So it will be passed in as string by "yes" or "no".
     * @return the changed product
     * @throws IllegalArgumentException
     */
    @PostMapping(value = {"/products/changeAvailability", "/products/changeAvailability/"})
    public ProductDTO changeProductAvailability(@RequestParam(name = "productName") String productName,
    									        @RequestParam(name = "isAviliableOnline") String isAviliableOnline){
    	Product p = service.getProductByName(productName);
    	if (p == null) {
    		throw new IllegalArgumentException("the product do not exsist");
    	}
    	if (!("yes".equals(isAviliableOnline) || "no".equals(isAviliableOnline))) {
    		throw new IllegalArgumentException("field isAviliableOnline needs to be yes or no");
    	}
    	service.deleteProduct(productName);
    	Product newProduct = service.createProduct(p.getPriceType(), p.getProductName(), isAviliableOnline, p.getPrice(),p.getStock());
        return convertToDTO(newProduct);
    }
    
    /**
     * This is an implementation of Req.14.
     * Req.14-The Grocery Store System shall allow the owner to create a sales report containing all orders and their respective totals 
     * @return all orders
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/orders", "/orders"})
    public List<OrderDTO> getAllOrders() throws IllegalArgumentException {
    	if (!"owner".equals(Project321BackendApplication.getUserType())) {
    		throw new IllegalArgumentException("only owner is able to view sales report.");
    	}
    	List<Order> list = service.getAllOrders();
    	if (list == null) {
    		throw new IllegalArgumentException("currently no orders in the system");
    	}
        return convertOrderListDTO(list);
    }
    
    /**
     * This is an enhancement to Req.14, which generates the sales total for the owner.
     * Req.14-The Grocery Store System shall allow the owner to create a sales report containing all orders and their respective totals 
     * @return sales total
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/orders/total", "/orders/total"})
    public int getAllOrdersTotal() throws IllegalArgumentException {
    	if (!"owner".equals(Project321BackendApplication.getUserType())) {
    		throw new IllegalArgumentException("only owner is able to view sales total.");
    	}
    	List<Order> list = service.getAllOrders();
    	if (list == null) {
    		throw new IllegalArgumentException("currently no orders in the system");
    	}
    	int sum = 0;
    	for (Order o: list) {
    		sum += o.getTotal();
    	}
    	return sum;
    }

    @GetMapping(value = {"/cart/checkout", "/cart/checkout/"})
    public int checkoutCart() throws IllegalAccessException, IllegalArgumentException{
        int totalPrice = 0;
        if(Project321BackendApplication.getUserType() != null && Project321BackendApplication.getUserType().contentEquals("customer")) {
            CartDTO localCart = Project321BackendApplication.getCart();
            if(localCart == null) {
                throw new IllegalAccessException("No cart to checkout!");
            }
            List<CartItemDTO> itemList = localCart.getCartItems();
            for(CartItemDTO i : itemList) {
                totalPrice += i.getQuantity() * i.getProduct().getPrice();
            }
            if(shippingFeeChecker()){
                totalPrice += Project321BackendApplication.getStore().getOutOfTownFee();
            }
            service.createOrder(false, java.sql.Date.valueOf(LocalDate.now()), 
                                totalPrice, null, convertToDomainObject(localCart));
            return totalPrice;     
        }
        else {
            throw new IllegalArgumentException("Must be logged in as a customer.");
        }
    }

    @PostMapping(value = {"/cart/pay", "/cart/pay/"})
    public boolean payCart(@RequestParam(name = "paymentcode") String paymentCode) 
                       throws IllegalAccessException{
        if(Project321BackendApplication.getUserType() != null && Project321BackendApplication.getUserType().contentEquals("customer")) {
            CartDTO localCart = Project321BackendApplication.getCart();
            if(localCart == null) {
                throw new IllegalAccessException("No cart to pay for!");
            }
            Order order = service.getOrderByCart(convertToDomainObject(localCart));
            service.setPayment(order, paymentCode);
            Project321BackendApplication.setCart(null); // Reset the cart to null since we are done handling the latest cart
            return true;     
        }
        else {
            throw new IllegalArgumentException("Must be logged in as a customer.");
        }
    }
    
    @GetMapping(value = {"/shifts", "/shifts/"})
    public List<ShiftDTO> getAllShift() throws IllegalArgumentException {
        return convertShiftListToDTO(service.getAllShifts());
    }
    
    /**
     * This is an implementation of the Req.01 
     * Req.01-"As a user of the Grocery software system with an employee account, I would like to be able to visualize my weekly work schedules."
     * @return list of shifts belongs to the current user(employee).
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/shifts/myshifts", "/shifts/myshifts/"})
    public List<ShiftDTO> getAllShiftbyEmployee() throws IllegalArgumentException {
    	if (!Project321BackendApplication.getUserType().equals("employee")) {
    		throw new IllegalArgumentException("you do not have an employee account loged in. Unable to retrive shifts.");
    	}
    	List<ShiftDTO> list = convertShiftListToDTO(service.getShiftByEmployee(convertToDomainObject((EmployeeDTO)Project321BackendApplication.getCurrentUser())));
        return list;
    }
    
    @PostMapping(value = {"/shifts", "/shifts/"})
    public ShiftDTO createShift(@RequestParam(name = "startHour") @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date startHour,
                              @RequestParam(name = "endHour") @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date endHour,
                              @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date date,
                              @RequestParam(name = "employeeEmail")  String email) 
                              throws IllegalArgumentException {
    	if (!"owner".equals(Project321BackendApplication.getUserType())) {
    		throw new IllegalArgumentException("only owner is able to create a shift.");
    	}
        Time startTime = new Time(startHour.getTime());
        Time endTime = new Time(endHour.getTime());
    	Shift shift = service.createShift(startTime, endTime, new java.sql.Date(date.getTime()), service.getEmployee(email));
        List<TimeSlot> timeSlotOverShift = service.getTimeSlotsBetween(startTime, endTime);
        for(TimeSlot t : timeSlotOverShift) {
            service.incrementMaxOrderPerslot(t);
        }
    	if (shift == null) {
    		throw new IllegalArgumentException("the shift with the same date for the employee already exsist.");
    	}
        return convertToDTO(shift);
    }
    
    @GetMapping(value = {"/address", "/address/"})
    public List<AddressDTO> getAllAddress() throws IllegalArgumentException {
    	List<Address> list = service.getAllAddresses();
    	if  (list == null) {
    		throw new IllegalArgumentException("There are currently no addresses in the system");
    	}
        return convertAddressListDTO(list);
    }
    
    @PostMapping(value = {"/address", "/address/"})
    public AddressDTO createAddress(@RequestParam(name = "town")     String town, 
                                      @RequestParam(name = "street")      String street, 
                                      @RequestParam(name = "postalcode")  String postalcode,
                                      @RequestParam(name = "unit")     int unit) 
    throws IllegalArgumentException {
    	Address a = service.createAddresses(town, street, postalcode, unit);
        return convertToDTO(a);
    }
    
    @PostMapping(value = {"/store", "/store/"})
    public StoreDTO createStore(@RequestParam(name = "telephone")     String telephone, 
                                      @RequestParam(name = "email")      String email, 
                                      @RequestParam(name = "openingHour")  @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date startHour,
                                      @RequestParam(name = "closingHour")  @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date endHour,
                                      @RequestParam(name = "town")     String town, 
                                      @RequestParam(name = "street")      String street, 
                                      @RequestParam(name = "postalcode")  String postalcode,
                                      @RequestParam(name = "unit")     int unit, 
                                      @RequestParam(name = "outoftownfee") int outOfTownFee)
    throws IllegalArgumentException, IllegalStateException{
        if(service.getStore() != null) {
            throw new IllegalStateException("A store already exits! Cannot create another");
        }
    	Address address = service.getAddresseByUnitAndStreetAndTownAndPostalCode(unit, street, town, postalcode);
        StoreOwner owner = service.getStoreOwner();
        if(owner == null) {
            throw new IllegalStateException("No store owner in the system, cannot create a store");
        }
    	AddressDTO addressDto;
    	if (address == null) {
    		addressDto = createAddress(town, street, postalcode, unit);
    	} else {
    		addressDto = convertToDTO(address);
    	}
    	Store s = service.createStore(telephone, email, new Time(startHour.getTime()), new Time(endHour.getTime()),
    			                      owner, convertToDomainObject(addressDto), outOfTownFee);
    	if (s == null) {
    		throw new IllegalArgumentException("there is a store at the location already.");
    	}
        return convertToDTO(s);
    }
    /**
     * This is an implementation of the Req.07
     * Req.07-The Grocery software system shall allow the owner to modify the opening date and opening hours.
     * @return the modified store
     * @throws IllegalArgumentException
     */
    @PostMapping(value = {"/store/changeHours", "/changeHours/"})
    public StoreDTO changeStoreHours(@RequestParam(name = "openingHour")  @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date startHour,
                                      @RequestParam(name = "closingHour")  @DateTimeFormat(pattern = "HH:mm:ss") java.util.Date endHour,
                                      @RequestParam(name = "town")     String town, 
                                      @RequestParam(name = "street")      String street, 
                                      @RequestParam(name = "postalcode")  String postalcode,
                                      @RequestParam(name = "unit")     int unit)
    throws IllegalArgumentException {
    	if (!"owner".equals(Project321BackendApplication.getUserType())) {
    		throw new IllegalArgumentException("only owner is able to change store hours.");
    	}
    	Address address = service.getAddresseByUnitAndStreetAndTownAndPostalCode(unit, street, town, postalcode);
    	AddressDTO addressDto;
    	if (address == null) {
    		addressDto = createAddress(town, street, postalcode, unit);
    	} else {
    		addressDto = convertToDTO(address);
    	}
    	Store s = service.getStore();
    	if (s == null) {
    		throw new IllegalArgumentException("there is no store at the location");
    	}
    	service.deleteStore(s);
    	s.setOpeningHour(new Time(startHour.getTime()));
    	s.setClosingHour(new Time(endHour.getTime()));
    	service.createStore(s);
        return convertToDTO(s);
    }
    
    @GetMapping(value = {"/store", "/store/"})
    public StoreDTO getStore() throws IllegalArgumentException {
    	Store store = service.getStore();
    	if  (store == null) {
    		throw new IllegalArgumentException("There are currently no any store in the system");
    	}
        return convertToDTO(store);
    }
    
    @PostMapping(value = {"/cart/item", "/cart/item/"})
    public CartItemDTO addItemToCart(@RequestParam(name = "productname") String productName,
                                     @RequestParam(name = "quantity") Integer quantity) 
                                     throws IllegalAccessException, IllegalArgumentException {
        CartDTO localCart = Project321BackendApplication.getCart();
        if(localCart == null) {
            throw new IllegalAccessException("No cart to add item to!");
        }
        Product product = service.getProductByName(productName);
        if(product == null) {
            throw new IllegalArgumentException("Failed to find the product with the following name: " + productName);
        }
        if(quantity < 1) {
            throw new IllegalArgumentException("Invalid quantity: must be above or equal to one, received " + quantity);
        } else if(quantity > product.getStock()) {
            throw new IllegalArgumentException("Invalid quantity: quantity demanded higher than available stock");
        }
        Cart cart = convertToDomainObject(localCart);
        if(cart == null) {
            throw new IllegalAccessException("Failed to find current cart in database!");
        }
        CartItem item = service.createCartItem(quantity, product, cart);
        List<CartItemDTO> itemList = localCart.getCartItems();
        CartItemDTO localItem = convertToDTO(item, localCart);
        itemList.add(localItem);
        localCart.setCartItems(itemList);
        Project321BackendApplication.setCart(localCart);
        product.setStock(product.getStock() - quantity);
        
        return localItem;
    }

    @PostMapping(value = {"/instorebill", "/instorebill/"})
    public InStoreBillDTO createInStoreBill() throws IllegalAccessException{
        if(Project321BackendApplication.getUserType() != null && 
            (Project321BackendApplication.getUserType().contentEquals("employee") || Project321BackendApplication.getUserType().contentEquals("storeowner"))) {
            Date purchaseDate = java.sql.Date.valueOf(LocalDate.now());
            InStoreBillDTO bill = new InStoreBillDTO(0, purchaseDate, null);
            Project321BackendApplication.setBill(bill);
            return bill;
        } else {
            throw new IllegalAccessException("Cannot create an in-store bill! You need to have employee or store owner clearance");
        }
    }

    @PostMapping(value = {"/instorebill/item", "/instorebill/item/"})
    public CartItemDTO addItemToBill(@RequestParam(name = "productname") String productName,
                                     @RequestParam(name = "quantity") Integer quantity) 
                                     throws IllegalAccessException, IllegalArgumentException {
        if(Project321BackendApplication.getUserType() != null && 
            (Project321BackendApplication.getUserType().contentEquals("employee") || Project321BackendApplication.getUserType().contentEquals("storeowner"))) {
            InStoreBillDTO localBill = Project321BackendApplication.getBill();
            if(localBill == null) {
                throw new IllegalAccessException("No bill to add item to");
            }
            Product product = service.getProductByName(productName);
            if(product == null) {
                throw new IllegalArgumentException("Failed to find the product with the following name: " + productName);
            }
            if(quantity < 1) {
                throw new IllegalArgumentException("Invalid quantity: must be above or equal to one, received " + quantity);
            }
            CartItemDTO localItem = new CartItemDTO(localBill, quantity, convertToDTO(product));
            List<CartItemDTO> items = localBill.getCartItems();
            items.add(localItem);
            localBill.setCartItems(items);
            Project321BackendApplication.setBill(localBill);
            return localItem;
        } else {
            throw new IllegalAccessException("Cannot add item to in-store bill! You need to have employee or store owner clearance");
        }
    }

    @PostMapping(value = {"/instorebill/pay", "/instorebill/pay/"})
    public InStoreBillDTO payInStoreBill(@RequestParam(name = "paymentcode") String paymentCode) 
                                        throws IllegalAccessException {
        if(Project321BackendApplication.getUserType() != null && 
            (Project321BackendApplication.getUserType().contentEquals("employee") || Project321BackendApplication.getUserType().contentEquals("storeowner"))) {
            InStoreBillDTO localBill = Project321BackendApplication.getBill();
            if(localBill == null) {
                throw new IllegalAccessException("No in-store bill to pay for!");
            }
            InStoreBill bill = service.createInStoreBill(localBill.getTotal(), localBill.getPurchaseDate(), paymentCode);
            int total = 0;
            List<CartItemDTO> localItems = localBill.getCartItems();
            List<CartItem> items = new ArrayList<CartItem>();
            for(CartItemDTO i : localItems) {
                total += i.getQuantity() * i.getProduct().getPrice();
                Product p = service.getProductByName(i.getProduct().getProductName());
                if(p == null) {
                    throw new IllegalStateException("Cannot seem to find product named " + i.getProduct().getProductName() 
                                                    + " in database anymore...");
                }
                p = service.setProductStock(i.getProduct().getProductName(), i.getQuantity());
                CartItem c = service.createCartItem(i.getQuantity(), p, bill);
                items.add(c);
            }
            service.setInStoreBillTotal(total, bill);
            return null;
        } else {
            throw new IllegalAccessException("Cannot add item to in-store bill! You need to have employee or store owner clearance");
        }
    }

    /* Helper methods ---------------------------------------------------------------------------------------------------- */
    private List<CustomerDTO> convertCustomerListToDTO(List<Customer> customers) throws IllegalArgumentException{
        List<CustomerDTO> list = new ArrayList<CustomerDTO>();
        for(Customer c : customers) {
            list.add(convertToDTO(c));
        }
        return list;
    }
    
    private List<EmployeeDTO> convertEmployeeListToDTO(List<Employee> employees) throws IllegalArgumentException{
        List<EmployeeDTO> list = new ArrayList<EmployeeDTO>();
        for(Employee e : employees) {
            list.add(convertToDTO(e));
        }
        return list;
    }

    private List<ShiftDTO> convertShiftListToDTO(List<Shift> shifts) throws IllegalArgumentException{
        List<ShiftDTO> list = new ArrayList<ShiftDTO>();
        for(Shift s : shifts) {
            list.add(convertToDTO(s));
        }
        return list;
    }
    
    private List<ProductDTO> convertProductListDTO(List<Product> products) throws IllegalArgumentException{
        List<ProductDTO> list = new ArrayList<ProductDTO>();
        for(Product p : products) {
            list.add(convertToDTO(p));
        }
        return list;
    }
    
    private List<OrderDTO> convertOrderListDTO(List<Order> orders) throws IllegalArgumentException{
        List<OrderDTO> list = new ArrayList<OrderDTO>();
        for(Order o : orders) {
            list.add(convertToDTO(o));
        }
        return list;
    }
    
    private List<AddressDTO> convertAddressListDTO(List<Address> address) throws IllegalArgumentException{
        List<AddressDTO> list = new ArrayList<AddressDTO>();
        for(Address a : address) {
            list.add(convertToDTO(a));
        }
        return list;
    }
    
    private List<StoreDTO> convertStoreListDTO(List<Store> stores) throws IllegalArgumentException{
        List<StoreDTO> list = new ArrayList<StoreDTO>();
        for(Store s : stores) {
            list.add(convertToDTO(s));
        }
        return list;
    }
    
    private List<InStoreBillDTO> convertInStoreBillListToDTO(List<InStoreBill> bills) throws IllegalArgumentException{
        List<InStoreBillDTO> list = new ArrayList<InStoreBillDTO>();
        for(InStoreBill isb : bills) {
            list.add(convertToDTO(isb));
        }
        return list;
    }
    
    private CustomerDTO convertToDTO(Customer customer) {
        if(customer == null) throw new IllegalArgumentException("Customer does not exist");
        AddressDTO address = convertToDTO(customer.getAddress());
        CustomerDTO c = new CustomerDTO(customer.getEmail(), customer.getName(), customer.getPassword(), 
                                        customer.getPhone(), address);
        return c;
    }
    
    private StoreOwnerDTO convertToDTO(StoreOwner storeOwner) {
        if(storeOwner == null) throw new IllegalArgumentException("Store Owner does not exist");
        StoreOwnerDTO so = new StoreOwnerDTO(storeOwner.getEmail(), storeOwner.getName(), storeOwner.getPassword());
        return so;
    }
    
    private EmployeeDTO convertToDTO(Employee employee) {
        if(employee == null) throw new IllegalArgumentException("Employee does not exist");
        EmployeeDTO e = new EmployeeDTO(employee.getEmail(), employee.getName(), employee.getPassword(), translateEnum(employee.getStatus()));
        return e;
    }

    private AddressDTO convertToDTO(Address address) {
        if(address == null) throw new IllegalArgumentException("Address does not exist");
        AddressDTO a = new AddressDTO(address.getTown(), address.getStreet(), address.getPostalCode(), address.getUnit());
        return a;
    }
    
    private ShiftDTO convertToDTO(Shift shift) {
        if(shift == null) throw new IllegalArgumentException("shift does not exist");
        ShiftDTO s = new ShiftDTO(shift.getStartHour(), shift.getEndHour(), shift.getDate(), convertToDTO(shift.getEmployee()));
        return s;
    }
    
    private OrderDTO convertToDTO(Order order) {
        if(order == null) throw new IllegalArgumentException("order does not exist");
        OrderDTO o = new OrderDTO(order.getCompleted(), order.getOrderDate(), order.getTotal(), order.getPayment(), convertToDTO(order.getCart()));
        return o;
    }
    
    
    private StoreDTO convertToDTO(Store store) {
        if(store == null) throw new IllegalArgumentException("store does not exist");
        StoreDTO s = new StoreDTO(store.getTelephone(), store.getEmail(), store.getOpeningHour(), 
        		                  store.getClosingHour(), convertToDTO(store.getStoreOwner()), convertToDTO(store.getAddress()),
                                  store.getOutOfTownFee());
        return s;
    }
    
    private InStoreBillDTO convertToDTO(InStoreBill bill) {
        if(bill == null) throw new IllegalArgumentException("store does not exist");
        InStoreBillDTO isb = new InStoreBillDTO(bill.getTotal(), bill.getPurchaseDate(), bill.getPaymentCode());
        return isb;
    }
    

    private Address convertToDomainObject(AddressDTO address) {
        List<Address> allAddresses = service.getAllAddresses();
        for( Address a : allAddresses ) {
            if( (address.getPostalCode().equals(a.getPostalCode())) && (address.getStreet().equals(a.getStreet())) && 
                (address.getTown().equals(a.getTown())) && (address.getUnit() == a.getUnit())) {
                    return a;
            }
        }
        return null;
    }
    
    private Employee convertToDomainObject(EmployeeDTO employee) {
        List<Employee> Employees = service.getAllEmployee();
        for( Employee e : Employees ) {
            if(e.getEmail().equals(employee.getEmail())) {
                    return e;
            }
        }
        return null;
    }
    
    private StoreOwner convertToDomainObject(StoreOwnerDTO ownerDto) {
        StoreOwner owner = service.getStoreOwner();
        return owner;
    }

    private List<CartDTO> convertCartListToDTO(List<Cart> carts) throws IllegalArgumentException{
        List<CartDTO> list = new ArrayList<CartDTO>();
        for(Cart c : carts) {
            list.add(convertToDTO(c));
        }
        return list;
    }

    private CartDTO convertToDTO(Cart cart) {
        if(cart == null) throw new IllegalArgumentException("Cart does not exist");
        TimeSlotDTO timeSlot = convertToDTO(cart.getTimeSlot());
        CustomerDTO customer = convertToDTO(cart.getCustomer());
        CartDTO.ShoppingTypeDTO type = translateEnum(cart.getType());
        if(type == null) throw new IllegalArgumentException("Invalid shopping type for cart");
        CartDTO c = new CartDTO(type, customer, cart.getCreationDate(), cart.getCreationTime(), timeSlot);
        c.setCartItems(createCartItemsList(cart, c));
        return c;
    }

    private List<TimeSlotDTO> convertTimeSlotListToDTO(List<TimeSlot> timeSlots) {
    	List<TimeSlotDTO> list = new ArrayList<TimeSlotDTO>();
        for(TimeSlot t : timeSlots) {
            list.add(convertToDTO(t));
        }
        return list;
    }
    
    private TimeSlotDTO convertToDTO(TimeSlot timeSlot) {
        if(timeSlot == null) return null;
        TimeSlotDTO t = new TimeSlotDTO(timeSlot.getDate(), timeSlot.getStartTime(), 
                                        timeSlot.getEndTime(), timeSlot.getMaxOrderPerSlot());
        return t;
    }

    private List<CartItemDTO> createCartItemsList(Cart cart, CartDTO cartDTO) {
        List<CartItemDTO> list = new ArrayList<CartItemDTO>();
        List<CartItem> items = service.getCartItemsByCart(cart);
        for( CartItem i : items ) {
            list.add(convertToDTO(i, cartDTO));
        }
        return list;
    }

    private CartItemDTO convertToDTO(CartItem cartItem, CartDTO cart) {
        if(cartItem == null) throw new IllegalArgumentException("Cart Item does not exist");
        ProductDTO product = convertToDTO(cartItem.getProduct());
        CartItemDTO i = new CartItemDTO(cart, cartItem.getQuantity(), product);
        return i;
    }

    private ProductDTO convertToDTO(Product product) {
        if(product == null) throw new IllegalArgumentException("Product does not exist");
        ProductDTO.PriceTypeDTO priceType = translateEnum(product.getPriceType());
        if(priceType == null) throw new IllegalArgumentException("Invalid price type for the product");
        ProductDTO p = new ProductDTO(priceType, product.getProductName(), product.getIsAvailableOnline(), 
                                        product.getStock(), product.getPrice());
        return p;
    }

    private ProductDTO.PriceTypeDTO translateEnum(Product.PriceType priceType) {
        switch(priceType) {
            case PER_KILOS:
                return PriceTypeDTO.PER_KILOS;
            case PER_UNIT:
                return PriceTypeDTO.PER_UNIT;
            default:
                return null;
        }
    }

    private Product.PriceType translateEnum(ProductDTO.PriceTypeDTO priceType) {
        switch(priceType) {
            case PER_KILOS:
                return PriceType.PER_KILOS;
            case PER_UNIT:
                return PriceType.PER_UNIT;
            default:
                return null;
        }
    }

    private CartDTO.ShoppingTypeDTO translateEnum(Cart.ShoppingType type) {
        switch(type) {
            case Delivery:
                return ShoppingTypeDTO.Delivery;
            case Pickup:
                return ShoppingTypeDTO.Pickup;
            default:
                return null;
        }
    }

    private Cart.ShoppingType translateEnum(CartDTO.ShoppingTypeDTO type) {
        switch(type) {
            case Delivery:
                return ShoppingType.Delivery;
            case Pickup:
                return ShoppingType.Pickup;
            default:
                return null;
        }
    }
    
    private EmployeeDTO.EmployeeStatusDTO translateEnum(Employee.EmployeeStatus status) {
        switch(status) {
	        case Sick:
	            return EmployeeStatusDTO.Sick;
	        case Inactive:
	            return EmployeeStatusDTO.Inactive;
	        case Active:
	            return EmployeeStatusDTO.Active;
	        default:
	            return null;
        }
    }
    
    private Employee.EmployeeStatus translateEnum(EmployeeDTO.EmployeeStatusDTO type) {
        switch(type) {
            case Sick:
                return EmployeeStatus.Sick;
            case Inactive:
                return EmployeeStatus.Inactive;
            case Active:
                return EmployeeStatus.Active;
            default:
                return null;
        }
    }

    private Customer convertToDomainObject(CustomerDTO customer) {
        List<Customer> allCustomers = service.getAllCustomers();
        for( Customer c : allCustomers ) {
            if( customer.getEmail().equals(c.getEmail()) ) {
                    return c;
            }
        }
        return null;
    }

    private TimeSlot convertToDomainObject(TimeSlotDTO timeSlot) {
        List<TimeSlot> allTimeSlots = service.getAllTimeSlots();
        for( TimeSlot t : allTimeSlots ){
            if(timeSlot.getDate().equals(t.getDate()) && timeSlot.getStartTime().equals(t.getStartTime()) &&
                timeSlot.getEndTime().equals(t.getEndTime())) {
                return t;
            }
        }
        return null;
    }

    private Cart convertToDomainObject(CartDTO cart) {
        List<Cart> allCarts = service.getAllCarts();
        for(Cart c : allCarts) {
            if(c.getCustomer().getEmail().equals(cart.getCustomer().getEmail()) &&
               c.getCreationDate().equals(cart.getCreationDate()) && c.getCreationTime().equals(cart.getCreationTime())) {
                   return c;
               }
        }
        return null;
    }
}
