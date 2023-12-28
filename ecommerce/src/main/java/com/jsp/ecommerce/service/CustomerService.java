package com.jsp.ecommerce.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.jsp.ecommerce.dao.CustomerDao;
import com.jsp.ecommerce.dao.ProductDao;
import com.jsp.ecommerce.dto.Customer;
import com.jsp.ecommerce.dto.Product;
import com.jsp.ecommerce.helper.AES;
import com.jsp.ecommerce.helper.EmailLogic;

import jakarta.servlet.http.HttpSession;


@Service
public class CustomerService {

	@Autowired
	CustomerDao customerDao;
	
	@Autowired
	EmailLogic emailLogic;

	@Autowired
	ProductDao productDao;

	public String signup(Customer customer, ModelMap map) {
		// to check Email and Mobile is Unique
		List<Customer> exCustomers = customerDao.findbyemailOrMobile(customer.getEmail(), customer.getMobile());
		if (!exCustomers.isEmpty()) {
			map.put("fail", "Account Already Exists");
			return "signup";
		} else {
			// Generating otp
			int otp = new Random().nextInt(100000, 999999);
			customer.setOtp(otp);
			// Encrypting password
			customer.setPassword(AES.encrypt(customer.getPassword(), "123"));
			customerDao.save(customer);
			// Send OTP to email
			emailLogic.sendOtp(customer);
			// Carrying id
			map.put("id", customer.getId());
			return "enterotp";
		}

	}
      public String verifyotp(int id,int otp,ModelMap map) {
    	   Customer customer=customerDao.findbyid(id);
    	   if(customer.getOtp()==otp) {
    		   customer.setVerified(true);
    		   customerDao.update(customer);
    		   map.put("pass", "Accout created scuessfully");
    		   return "login.html";
    	   }else {
    		   map.put("fail", "Invalid Otp, Try Agin");
    		   map.put("id", id);
    		   return "enterotp";
    	   }
      }
      public String login(String emph, String password, ModelMap map, HttpSession session) {
  		if(emph.equals("admin") && password.equals("admin"))
  		{
  			session.setAttribute("admin", "admin");
  			map.put("pass", "Admin Login Success");
  			return "adminhome";
  		}
  		else {
  			long mobile=0;
  			String email=null;
  			try {
  			mobile=Long.parseLong(emph);
  			}
  			catch (NumberFormatException e) {
  				email=emph;
  			}
  			
  			List<Customer> customers=customerDao.findbyemailOrMobile(email, mobile);
  			if(customers.isEmpty())
  			{
  				map.put("fail", "Invalid Email or Mobile");
  				return "login.html";
  			}
  			else {
  				Customer customer=customers.get(0);
  				if(AES.decrypt(customer.getPassword(),"123").equals(password))
  				{
  					if(customer.isVerified())
  					{
  						map.put("pass", "Login Success");
  						return "customerhome";
  					}
  					else {
  						int otp = new Random().nextInt(100000, 999999);
  						customer.setOtp(otp);
  						customerDao.save(customer);
  						// Send OTP to email
  						// emailLogic.sendOtp(customer);
  						// Carrying id
  						map.put("fail", "Verify First");
  						map.put("id", customer.getId());
  						return "enterotp";
  					}
  				}
  				else {
  					map.put("fail", "Invalid Password");
  					return "login.html";
  				}
  			}
  		}
  	}
      
      public String fetchProducts(ModelMap map) {
  		List<Product> products = productDao.fetchDisplayProducts();
  		if (products.isEmpty()) {
  			map.put("fail", "No Products Present");
  			return "customerhome";
  		} else {
  			map.put("products", products);
  			return "customevriewproduct";
  		}
  	}
	
	
	

}
