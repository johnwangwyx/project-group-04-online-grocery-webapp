/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.31.1.5860.78bb27cc6 modeling language!*/


import java.sql.Date;

// line 47 "model.ump"
// line 158 "model.ump"
public class Order
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Order Attributes
  private int orderID;
  private boolean completed;
  private Date orderDate;
  private int total;
  private String payment;

  //Order Associations
  private Cart cart;
  private TheGroceryStoreSystem theGroceryStoreSystem;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Order(int aOrderID, boolean aCompleted, Date aOrderDate, int aTotal, String aPayment, Cart aCart, TheGroceryStoreSystem aTheGroceryStoreSystem)
  {
    orderID = aOrderID;
    completed = aCompleted;
    orderDate = aOrderDate;
    total = aTotal;
    payment = aPayment;
    boolean didAddCart = setCart(aCart);
    if (!didAddCart)
    {
      throw new RuntimeException("Unable to create order due to cart. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddTheGroceryStoreSystem = setTheGroceryStoreSystem(aTheGroceryStoreSystem);
    if (!didAddTheGroceryStoreSystem)
    {
      throw new RuntimeException("Unable to create order due to theGroceryStoreSystem. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setOrderID(int aOrderID)
  {
    boolean wasSet = false;
    orderID = aOrderID;
    wasSet = true;
    return wasSet;
  }

  public boolean setCompleted(boolean aCompleted)
  {
    boolean wasSet = false;
    completed = aCompleted;
    wasSet = true;
    return wasSet;
  }

  public boolean setOrderDate(Date aOrderDate)
  {
    boolean wasSet = false;
    orderDate = aOrderDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setTotal(int aTotal)
  {
    boolean wasSet = false;
    total = aTotal;
    wasSet = true;
    return wasSet;
  }

  public boolean setPayment(String aPayment)
  {
    boolean wasSet = false;
    payment = aPayment;
    wasSet = true;
    return wasSet;
  }

  public int getOrderID()
  {
    return orderID;
  }

  public boolean getCompleted()
  {
    return completed;
  }

  public Date getOrderDate()
  {
    return orderDate;
  }

  public int getTotal()
  {
    return total;
  }

  public String getPayment()
  {
    return payment;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isCompleted()
  {
    return completed;
  }
  /* Code from template association_GetOne */
  public Cart getCart()
  {
    return cart;
  }
  /* Code from template association_GetOne */
  public TheGroceryStoreSystem getTheGroceryStoreSystem()
  {
    return theGroceryStoreSystem;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCart(Cart aCart)
  {
    boolean wasSet = false;
    if (aCart == null)
    {
      return wasSet;
    }

    Cart existingCart = cart;
    cart = aCart;
    if (existingCart != null && !existingCart.equals(aCart))
    {
      existingCart.removeOrder(this);
    }
    cart.addOrder(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setTheGroceryStoreSystem(TheGroceryStoreSystem aTheGroceryStoreSystem)
  {
    boolean wasSet = false;
    if (aTheGroceryStoreSystem == null)
    {
      return wasSet;
    }

    TheGroceryStoreSystem existingTheGroceryStoreSystem = theGroceryStoreSystem;
    theGroceryStoreSystem = aTheGroceryStoreSystem;
    if (existingTheGroceryStoreSystem != null && !existingTheGroceryStoreSystem.equals(aTheGroceryStoreSystem))
    {
      existingTheGroceryStoreSystem.removeOrder(this);
    }
    theGroceryStoreSystem.addOrder(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Cart placeholderCart = cart;
    this.cart = null;
    if(placeholderCart != null)
    {
      placeholderCart.removeOrder(this);
    }
    TheGroceryStoreSystem placeholderTheGroceryStoreSystem = theGroceryStoreSystem;
    this.theGroceryStoreSystem = null;
    if(placeholderTheGroceryStoreSystem != null)
    {
      placeholderTheGroceryStoreSystem.removeOrder(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "orderID" + ":" + getOrderID()+ "," +
            "completed" + ":" + getCompleted()+ "," +
            "total" + ":" + getTotal()+ "," +
            "payment" + ":" + getPayment()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "orderDate" + "=" + (getOrderDate() != null ? !getOrderDate().equals(this)  ? getOrderDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "cart = "+(getCart()!=null?Integer.toHexString(System.identityHashCode(getCart())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "theGroceryStoreSystem = "+(getTheGroceryStoreSystem()!=null?Integer.toHexString(System.identityHashCode(getTheGroceryStoreSystem())):"null");
  }
}