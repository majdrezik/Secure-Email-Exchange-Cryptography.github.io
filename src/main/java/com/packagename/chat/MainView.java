package com.packagename.chat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinRequest;

import DAO.AES;
import DAO.ElGamalSignatureInstance;
import DAO.RSAImpl;
import DAO.Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@SuppressWarnings("serial")
@Route("")
@Push
@PWA(name = "CryptoEmail",
        shortName = "CryptoEmail",
        description = "This is a simple Email Exchange App.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@StyleSheet("frontend://styles/shared-styles.css")
public class MainView extends VerticalLayout {
 
	

	private UnicastProcessor<ChatMessage> publisher;
	private Flux<ChatMessage> messages;
    public String _from, _to, _cc, _subject, _body, username, time; // colorSentReceived;;
	User Bob = new User("Bob","123");
	User Alice = new User("ALice","123");
    HorizontalLayout mainLayout = new HorizontalLayout();
    byte[] globalKey = "This is a key".getBytes();
	public RSAImpl RSA;
	ElGamalSignatureInstance instance; //= new ElGamalSignatureInstance();
//	private AES AESInstance = new AES();
    
	//Map each user to its 'e'. to get 'n', call getN()
	public Map<User, BigInteger> RSApublicKeys = new HashMap<>();
	User specUser;
	User fromUser;
	/*
	 * 	p = new BigInteger("5700734181645378434561188374130529072194886062117");
    q = new BigInteger("35894562752016259689151502540913447503526083241413");
    e = new BigInteger("33445843524692047286771520482406772494816708076993");
	 */

    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    
    public MainView(UnicastProcessor<ChatMessage> publisher, Flux<ChatMessage> messages) {
    	this.publisher = publisher;
    	this.messages = messages;
    	mainLayout.setWidth("100%");
    	setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	setSizeFull(); 
    	addClassName("main-view");
    	H1 header = new H1("CryptoEmail");
    	header.getElement().getThemeList().add("dark");
    	

    	

    	Bob.setRSA(new BigInteger("5700734181645378434561188374130529072194886062117"),
    			new BigInteger("35894562752016259689151502540913447503526083241413"),
    			new BigInteger("33445843524692047286771520482406772494816708076993"));
    	
    	Alice.setRSA(new BigInteger("35894562752016259689151502540913447503526083241413"),
    			new BigInteger("5700734181645378434561188374130529072194886062117"),
    			new BigInteger("33445843524692047286771520482406772494816708076993"));
    	
    	
    	RSApublicKeys.put(Bob, Bob.getE());
    	RSApublicKeys.put(Alice, Alice.getE());
    	
    
    	
    	add(header);
    	askUsername();

    }

	
 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    
     
    private void askUsername() {
		HorizontalLayout usernameLayout = new HorizontalLayout();

		LoginForm component = new LoginForm();
		component.addLoginListener(e -> {
		username = e.getUsername();
		String password = e.getPassword();
		boolean isUserFound, isPasswordRight =false, isAuthenticated;
	   isUserFound = username.equalsIgnoreCase(Bob.getUserName()) || username.equalsIgnoreCase(Alice.getUserName()) ? true : false;
	   
	   if(isUserFound) {
		   if(username.equalsIgnoreCase(Bob.getUserName())) {
			   isPasswordRight = password.equalsIgnoreCase(Bob.getPassword()) ? true : false;
		   } else {
			   isPasswordRight = password.equalsIgnoreCase(Alice.getPassword()) ? true : false;
		   }
	   }
	   isAuthenticated = isUserFound && isPasswordRight;
	    if (isAuthenticated) {
			remove(usernameLayout);
			//set progressBar 
			ProgressBar progressBar = new ProgressBar();
			progressBar.setIndeterminate(true);
			add(progressBar);	
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			remove(progressBar);
			getSpecUser();
	        showChat();
	    	
	    	
	    } else {
	        component.setError(true);
	    }
		});

		add(component);
		
		usernameLayout.add(component);
		add(usernameLayout);
	}


    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    String getTime() {
    	SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
    	Date date = new Date(System.currentTimeMillis());
    	return formatter.format(date);
    }

    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    
    private String getToOnGUI(ChatMessage message) {
    	if(message.getTo().equalsIgnoreCase(username))
    		return "me";
    	return message.getTo();
    }
    
    private String getFromOnGUI(ChatMessage message) {
    	if(message.getFrom().equalsIgnoreCase(username))
    		return "me";
    	return message.getFrom();
    }
    
    private String getSentOrReceived(ChatMessage message) {
    	if(message.getFrom().equalsIgnoreCase(username)) {
//    		setColor("green");
    		return "SENT";
    	}
//		setColor("blue");
    	return "RECEIVED";
    }
    
//    private void setColor(String color) {
//    	colorSentReceived = color;
//    	System.out.println(colorSentReceived);
//    }
    
    
    public User getOtherUser(User user) {
    	return user.getUserName().equals(Bob.getUserName()) ? Alice : Bob;
    }
    
    
	public User getSpecUser() {
		return (specUser = username.equals(Bob.getUserName()) ? Bob : Alice);
	}
    
    
	private void showChat() {
		
		//HorizontalLayout inputLayout = new HorizontalLayout();
		Button logOutButton = new Button("Log out");
		logOutButton.getElement().getThemeList().add("primary");
		
		logOutButton.setWidth("10%");
		
				
		MessageList messageList = new MessageList();
		messageList.setWidth("100%");
		messageList.setHeight("100%");
		messageList.getStyle().set("max-height", "90vh");
		messageList.getStyle().set("overflow-y", "auto");
		messageList.getStyle().set("padding-right", "8px");
		messageList.getStyle().set("padding-left", "8px");

		VerticalLayout vertical = new VerticalLayout();


		
		expand(messageList); 
		
		RSA = new RSAImpl(specUser.getP(), specUser.getQ(), specUser.getE());

		
		/*
			messages.subscribe(message -> {
			getUI().ifPresent(ui -> 
				ui.access(()->{
					//The thing that takes in the runnable
					messageList.add(
					new Paragraph(" " + getTime()),
					new Paragraph(" From : " + message.getFrom()),
					new Paragraph(" To : " + message.getTo()),
					new Paragraph(" Cc : " + message.getCc()),
					new Paragraph(" Subject : " + message.getSubject()),
					new Paragraph(" Body : " + message.getBody())
					);
					Paragraph p = new Paragraph();
					p.getStyle().set("borderBottom", "dotted 1px black");
					messageList.add(p);
					messageList.add(new Html("<br>"));

				}));
			});
				*/
		
		
	
		
				
		messages.subscribe(message -> {
			getUI().ifPresent(ui -> 
				ui.access(()->{
					//The thing that takes in the runnable
					User recievedUser = message.getTo().equals(Bob.getUserName()) ? Bob : Alice;

					if(!message.equals(null))
						messageList.add(
								
							new Html("<div class=\"card\" style=\"width: 18rem;\">\r\n" + 							  
									"							  <div class=\"card-body\">\r\n" + 
									"								<p class=\"card-title\" style= \"padding-left:20px;border-style: outset;\">" + "<i><b> "+  getSentOrReceived(message) + "</b></i>" + "</p>"+
									"								<p class=\"card-text\" style= \"padding-left:20px\"> " + getTime() + "</p>" + 
									"							    <h4 class=\"card-title\" style= \"padding-left:20px\">" + message.getSubject() + "</h4>\r\n"  +
									"								 <p class=\"card-text\"style= \"padding-left:20px\">From: " + getFromOnGUI(message) + "</p>\r\n" + 
									"							    <p class=\"card-text\"style= \"padding-left:20px\">To: " + getToOnGUI(message)  + "</p>\r\n" + 
	//								"							    <h4 class=\"card-title\" style= \"padding-left:20px\">" + (specUser.getUserName().equals(message.getFrom()) ? _body : new String(AES.ecb_decrypt(message.getBody(), Utils.bigIntegerToString(RSA.decrypt(message.getRSAKeyEncryption(), getOtherUser(specUser).getD(), getOtherUser(specUser).getN() )).getBytes()))) + "</h4>\r\n"  +
	"														        <h4 class=\"card-title\" style= \"padding-left:20px\">" + ((getSentOrReceived(message).equals("SENT")) ? _body : new String(AES.ecb_decrypt(message.getBody(), Utils.bigIntegerToString(RSA.decrypt(message.getRSAKeyEncryption(),  recievedUser.getD(), recievedUser.getN() )).getBytes()))) + "</h4>\r\n"  +
	
									"							  </div>\r\n" + 
									"							</div>"
									)
				
						);
						Paragraph p = new Paragraph();
						p.getStyle().set("borderBottom", "dotted 1px black");
						
						messageList.add(p);
						messageList.add(new Html("<br>"));
					}));
			});
				
		logOutButton.addClickListener(click -> {
			remove(mainLayout);
			mainLayout.removeAll();
			askUsername();
		});
		
		VerticalLayout ver = new VerticalLayout();
		ver.setWidth("10%");
		//ver.add( new Html("<h3>" + username + "</h3"), logOutButton);
		
		ver.add(new Html("<h3>" + username + "</h3>") , logOutButton);
		mainLayout.add(messageList,createInputLayout(), ver);

		//mainLayout.add(messageList,createInputLayout(), logOutButton);
		//mainLayout.add(new Span(username));

		add(mainLayout);

	}

	
	   
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	
	private Component createInputLayout() {
		HorizontalLayout inputLayout = new HorizontalLayout();
		inputLayout.setWidth("50%"); 
		inputLayout.setHeight("100%");

		

		TextField to = new TextField("To");
		//TextField cc = new TextField("Cc");
		TextField subject = new TextField("Subject");
		TextField body = new TextField("Body");

		VerticalLayout vertical = new VerticalLayout();
		
		
		
		to.setPlaceholder("To");
	//	cc.setPlaceholder("Cc");
		subject.setPlaceholder("Subject");
		body.setPlaceholder("Body");

		Button sendButton = new Button("Send");
		sendButton.getElement().getThemeList().add("primary");
		
		
		to.setWidth("100%");
		//cc.setWidth("100%");
		subject.setWidth("100%");
		body.setWidth("100%");
		
        vertical.add(to);
      //  vertical.add(cc);
        vertical.add(subject);
        vertical.add(body);
        vertical.add(sendButton);
        
       // vertical.setHorizontalComponentAlignment(Alignment.STRETCH , vertical);
       // vertical.setHorizontalComponentAlignment(Alignment.CENTER, vertical);;
        vertical.setWidth("100%");
        inputLayout.add(vertical);
		//inputLayout.expand(body);
        
		//inputLayout.setWidth("60px");
		to.focus();
		
		sendButton.addClickListener(click -> {
			
			_from = username;
			_to = to.getValue();
		//	_cc = cc.getValue ()+ "\n";
			_subject = subject.getValue() + "\n";
			_body = body.getValue() + "\n";

			boolean isUserValid = false, isPopUp=false, isErrorMessageChanged = false;
			
			
			//isUserValid = _to.equalsIgnoreCase(user1.getUserName()) || _to.equalsIgnoreCase(user2.getUserName()) ? true : false;
		//	isUserValid = _to.contains("user") ? true : false;
			String errorMessage = "' " + _to + " ' is unreachable.";
			if(_from.equalsIgnoreCase(Bob.getUserName())) {
				if(_to.equalsIgnoreCase(Alice.getUserName())) {
					isUserValid = true;
				}else if( !_to.equals("") && !_to.isEmpty() && !_to.equals(" ")){
					errorMessage += "You can only send to: ' " + Alice.getUserName() + " '. Close me with the esc-key or an outside click";	
					isErrorMessageChanged = true;
				}
			}else if(_from.equalsIgnoreCase(Alice.getUserName())) {
					if(_to.equalsIgnoreCase(Bob.getUserName())) {
						isUserValid = true;
					} else if( !_to.equals("") && !_to.isEmpty() && !_to.equals(" ")){
						errorMessage += "You can only send to: '" + Bob.getUserName() + " '. Close me with the esc-key or an outside click";
						isErrorMessageChanged = true;
					}
			}
			
			if(!isUserValid && _to instanceof String && isErrorMessageChanged) {
				isPopUp = true;
				Dialog dialog = new Dialog();
				dialog.add(new Text(errorMessage));
				dialog.setWidth("400px");
				dialog.setHeight("150px");
				dialog.open();
			}
	
			
			if((_to.isEmpty() || _to.equals(" ") || _to.equals(null)) && !isPopUp && !isErrorMessageChanged) {
				Dialog dialog = new Dialog();
				dialog.add(new Text("*To* Field can't be empty.					\n Close me with the esc-key or an outside click"));
				dialog.setWidth("400px");
				dialog.setHeight("150px");
				dialog.open();
			}
			
			
			
			
			
			if(isUserValid) {
				byte[] aesKey = AES.generateKey(15).getBytes();
				
				fromUser = username.equals(Bob.getUserName())? Bob : Alice;
				User toUser = getOtherUser(fromUser);

				List<BigInteger>  RSAKeyEncryption = RSA.encryptMessage(
						new String(aesKey), toUser.getE(), toUser.getN());//aesKey
				
//				byte[] bodyFieldEncrypted = AES.ecb_encrypt(_body.getBytes(), globalKey); this works!!!!!
				
				byte[] bodyFieldEncrypted = AES.ecb_encrypt(_body.getBytes(), aesKey);//aesKey
				
				//test
				System.out.println("AES: " + new String(bodyFieldEncrypted) );
				
				publisher.onNext(new ChatMessage(_from, _to , _cc,_subject, bodyFieldEncrypted, RSAKeyEncryption));
				
//				publisher.onNext(new ChatMessage(_from, _to , _cc,_subject, bodyFieldEncrypted));  this works!!!!!!!
				
				//publisher.onNext(new ChatMessage(_from, _to, _cc,_subject,_body));
				//clear fields
				body.clear(); //clear the message field and get back the placeholder.
				to.clear();
			//	cc.clear();
				subject.clear();
				to.focus(); //focus on message field so the user can continue typing.
				
			}			
		});
			
		to.focus(); //focus on message field so the user can continue typing.
		//add(inputLayout);
		return inputLayout;
	}

		
}

